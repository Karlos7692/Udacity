package com.nelson.karl.popularmovies.data.model.orm;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Karl on 13/01/2016.
 * We are not supporting many to many relationship. ObjectModelList -> ObjectModelList.
 */
public class ObjectModelList<T extends ObjectModel> extends ArrayList<T> implements ORM {

    private Uri mQueryUri;

    public ObjectModelList( Uri queryUri ) {
        mQueryUri = queryUri;
    }

    public static <T extends ObjectModel> ObjectModelList<T> get( ObjectRetrieverStrategy<T> strategy, Uri queryUri, Cursor cursor )
    {
        ObjectModelList<T> list = new ObjectModelList<>(queryUri);
        T model;
        while ( ( model = strategy.apply(cursor)) != null ) {
            if ( !list.contains(model) ) {
                list.add(model);
            }
            cursor.moveToNext();
        }
        return list;
    }

    @Override
    public void insert(Context context) {
        // Cannot insert into db without a query uri.
        if ( mQueryUri == null ) { return; }

        ContentValues[] values = new ContentValues[this.size()];
        for ( int i=0; i < this.size(); i++ ) {
            values[i] = this.get(i).toContentValues();
        }
        context.getContentResolver().bulkInsert(mQueryUri, values);
    }

    public void update(Context context) {
        // Cannot update db without query uri.
        if ( mQueryUri == null ) { return; }

        for ( int i=0; i < this.size(); i++ ) {
            T model = this.get(i);
            context.getContentResolver().update(model.getUri(), model.toContentValues(),
                    model.getSelection(), model.getDBIdentifierValues());
        }

    }
    @Override
    public void delete( Context context ) {
        for ( T model : this ) {
            model.delete( context );
        }
    }

    public Uri getUri() {
        return mQueryUri;
    }

    public void setUri( Uri queryUri ) {
        mQueryUri = queryUri;
    }

    /**
     * Assume that the db identifiers are correct, we should only get a set of objects matching
     * the db identifiers. We thus recursively merge each structure according to the external list.
     * Note: No functionality for ObjectModelList<Movie> since the bulk insert is not defined in the
     * content provider.
     * @param externalModels
     */
    public void merge(Context context, ObjectModelList<T> externalModels) {

        ObjectModelList<T> modelsToInsert = getMissingModels(externalModels);
        modelsToInsert.insert(context);

        //TODO finish delete code
        ObjectModelList<T> modelsToRemove = getAdditionalModels(externalModels);
        modelsToRemove.delete(context);

        ObjectModelList<T> similarModels = getSimilarInternalModels(externalModels);
        this.clear();

        // Maintain order of external list. Cannot assume order hence O(n^2) merge.
        for (T externalModel : externalModels) {
            if (similarModels.contains(externalModel)) {

                T model = getObjectModel(externalModel, similarModels);
                if (model == null) {
                    continue;
                }
                model.merge(context, externalModel);
                this.add(model);

            } else if (modelsToInsert.contains(externalModel)) {
                this.add(externalModel);
            }
        }
    }

    private T getObjectModel(ObjectModel model, ObjectModelList<T> list) {
        for ( T item : list ) {
            if ( model.equals(item) ) {
                return item;
            }
        }
        return null;
    }

    public ObjectModelList<T> getMissingModels(ObjectModelList<T> external) {
        Set<T> differentExternalModels = new HashSet<>(external);
        differentExternalModels.removeAll(this);
        ObjectModelList<T> missingModels = new ObjectModelList<>(external.getUri());
        missingModels.addAll(differentExternalModels);
        return missingModels;
    }

    public ObjectModelList<T> getAdditionalModels(ObjectModelList<T> external) {
        Set<T> differentInternalModels = new HashSet<>(this);
        differentInternalModels.removeAll(external);
        ObjectModelList<T> additionalModels = new ObjectModelList<>(this.getUri());
        additionalModels.addAll(differentInternalModels);
        return additionalModels;
    }

    private ObjectModelList<T> getSimilarInternalModels(ObjectModelList<T> external) {
        ObjectModelList<T> similarModels = new ObjectModelList<>(this.getUri());
        similarModels.addAll(this);
        similarModels.retainAll(external);
        return similarModels;
    }
}
