package info.camposha.terms.infrastructure.data.repository;

import android.annotation.SuppressLint;
import android.content.Context;

import java.util.List;

import info.camposha.terms.domain.entity.Term;
import info.camposha.terms.domain.usecase.ICallbacks;
import info.camposha.terms.infrastructure.data.db.MyRoomDB;
import info.camposha.terms.infrastructure.data.db.TermsDAO;
import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class TermsRepository implements ICallbacks.ICrud {
    private TermsDAO termsDAO;

    /**
     * Let's receive a context that will help with the instantiation of our MyRoomDB
     * @param context
     */
    public TermsRepository(Context context) {
        MyRoomDB myRoomDB = MyRoomDB.getInstance(context);
        termsDAO = myRoomDB.termsDAO();
    }

    /**
     * Insert into Room database
     * @param dataCallback
     * @param term
     */
    @Override
    public void insert(final ICallbacks.ISaveListener dataCallback, final Term term) {
        Completable.fromAction(() -> termsDAO.insert(term)).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }
                    @Override
                    public void onComplete() {
                        dataCallback.onSuccess("INSERT SUCCESSFUL");
                    }
                    @Override
                    public void onError(Throwable e) {
                        dataCallback.onError(e.getMessage());
                    }
                });
    }

    /**
     * Update our data
     * @param dataCallback
     * @param term
     */
    @Override
    public void update(final ICallbacks.ISaveListener dataCallback, final Term term) {
        Completable.fromAction(() -> termsDAO.update(term)).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }
                    @Override
                    public void onComplete() {
                        dataCallback.onSuccess("UPDATE SUCCESSFUL");
                    }
                    @Override
                    public void onError(Throwable e) {
                        dataCallback.onError(e.getMessage());
                    }
                });

    }

    /**
     * Delete from our Room database
     * @param dataCallback
     * @param term
     */
    @Override
    public void delete(final ICallbacks.ISaveListener dataCallback, final Term term) {
        Completable.fromAction(() -> termsDAO.delete(term)).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }
                    @Override
                    public void onComplete() {
                        dataCallback.onSuccess("DELETE SUCCESSFUL");
                    }
                    @Override
                    public void onError(Throwable e) {
                        dataCallback.onError(e.getMessage());
                    }
                });
    }

    /**
     * Select or retrieve our data
     * @param dataCallback
     */
    @Override
    public void selectAll(final ICallbacks.IFetchListener dataCallback) {
        termsDAO.selectAll().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(terms -> dataCallback.onDataFetched(terms));
    }

    /**
     * This method will allow us to select while paginating data at the SQLite level
     * @param start - where pagination is starting
     * @param limit - number of rows to fetch
     * @param iFetchListener
     */
    @Override
    public void selectAndPaginate(int start,int limit,ICallbacks.IFetchListener iFetchListener) {
        termsDAO.selectAndPaginate(start,limit).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(terms -> iFetchListener.onDataFetched(terms));
    }

    /**
     * Search Terms based on a Query
     * @param searchTerm
     * @param iFetchListener
     */
    @Override
    public void search(String searchTerm,ICallbacks.IFetchListener iFetchListener) {
        termsDAO.search("%"+searchTerm+"%").subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(terms -> iFetchListener.onDataFetched(terms));
    }

    /**
     * Select Terms by Date
     * @param date
     * @param iFetchListener
     */
    @SuppressLint("CheckResult")
    @Override
    public void selectByDate(String date, ICallbacks.IFetchListener iFetchListener) {
        termsDAO.selectByDate(date).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(terms -> iFetchListener.onDataFetched(terms));
    }
}
