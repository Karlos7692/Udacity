package com.nelson.karl.popularmovies.data.model.orm;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.List;

/**
 * Created by Karl on 4/01/2016.
 * Implementation of the ORM.
 */
public abstract class ObjectModel implements ORM {

    private static final String SELECTION_FORMAT = "%s = ? ";

    public static <T extends ObjectModel> T get( ObjectRetrieverStrategy<T> strategy, Cursor cursor ) {
        return strategy.apply( cursor );
    }

    @Override
    public void insert( Context context ) {
        context.getContentResolver().insert(this.getUri(), this.toContentValues());
        List<ORM> orms = getHasRelations();
        if ( orms != null ) {
            for ( ORM orm : orms ) {
                orm.insert( context );
            }
        }
    }

    public void update( Context context ) {
        context.getContentResolver().update(this.getUri(), this.toContentValues(),
                getSelection(), getDBIdentifierValues() );
        List<ORM> orms = getHasRelations();
        if ( orms != null ) {
            for (ORM orm : orms ) {
                orm.update(context);
            }
        }
    }
    public abstract ContentValues toContentValues();

    public abstract Uri getUri();

    public abstract String[] getDBIdentifiers();

    public abstract String[] getDBIdentifierValues();

    //TODO add better delete methods.
    public void delete( Context context ) {
        context.getContentResolver().delete(getUri(), getSelection(), getDBIdentifierValues());
    }

    public String getSelection() {
        String[] identifiers = getDBIdentifiers();
        StringBuilder sb = new StringBuilder();
        for ( int i=0; i<identifiers.length; i++) {
            sb.append(String.format(SELECTION_FORMAT, identifiers[i]));
            if ( i < identifiers.length -1 ) {
                sb.append(" AND ");
            }
        }
        return sb.toString();
    }

    /**
     * Get the "has" relationships that this Object model contains.
     * eg. A has many B. Return Many<B>.
     * @return
     */
    public abstract List<ORM> getHasRelations();
}
