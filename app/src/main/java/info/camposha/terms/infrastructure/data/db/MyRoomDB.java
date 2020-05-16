package info.camposha.terms.infrastructure.data.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import info.camposha.terms.domain.entity.Term;

@Database(entities = {Term.class}, version = 1, exportSchema = false)
public abstract class MyRoomDB extends RoomDatabase {

    private static MyRoomDB myRoomDB;

    public abstract TermsDAO termsDAO();

    /**
     *This factory method will instantiate for us our MyRoomDB class
     * @param c - A Context Object
     * @return
     */
    public static MyRoomDB getInstance(Context c) {
        if (myRoomDB == null) {
            myRoomDB = Room.databaseBuilder(c, MyRoomDB.class,
                    "MyRoomDB")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return myRoomDB;
    }
}
//end