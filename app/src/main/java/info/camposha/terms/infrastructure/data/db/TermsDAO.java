package info.camposha.terms.infrastructure.data.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import info.camposha.terms.domain.entity.Term;
import io.reactivex.Flowable;

@Dao
public interface TermsDAO {
    //NB= Methods annotated with @Insert can return either void, long, Long, long[],
    // Long[] or List<Long>.
    @Insert
    void insert(Term term);
    //Update methods must either return void or return int (the number of updated rows).
    @Update
    void update(Term term);
    //Deletion methods must either return void or return int (the number of deleted rows).
    @Delete
    void delete(Term term);

    /**
     * Select all terms and order them by dates
     * @return
     */
    @Query("SELECT * FROM TermsTB ORDER BY date")
    Flowable<List<Term>> selectAll();

    /**
     * Select terms,order them by dates and paginate them
     * @return
     */
    @Query("SELECT * FROM TermsTB ORDER BY id DESC LIMIT :limit OFFSET :start")
    Flowable<List<Term>> selectAndPaginate(int limit,int start);

    /**
     * Select terms filtering them by dates
     * @param thisDate
     * @return
     */
    @Query("SELECT * FROM TermsTB WHERE date LIKE :thisDate")
    Flowable<List<Term>> selectByDate(String thisDate);

    /**
     * Search terms based in a query
     * @return
     */
    @Query("SELECT * FROM TermsTB WHERE term LIKE :searchTerm")
    Flowable<List<Term>> search(String searchTerm);

    //NB= Deletion methods must either return void or return int (the number of deleted
    // rows).
    @Query("delete from TermsTB")
    void deleteAll();

}
