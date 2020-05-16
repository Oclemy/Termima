package info.camposha.terms.infrastructure.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import info.camposha.terms.domain.entity.Term;
import info.camposha.terms.domain.usecase.ICallbacks;
import info.camposha.terms.infrastructure.data.repository.TermsRepository;

public class MainViewModel extends AndroidViewModel {
    private TermsRepository termsRepository;
    public String SELECTED_DATE = "";
    public boolean IS_DAILY_VIEW = true;

    public MainViewModel(@NonNull Application application) {
        super(application);
        termsRepository =new TermsRepository(application);
    }

    /**
     * This method will:
     * 1. Insert our lesson into our SQLite database
     */
    public void insert(ICallbacks.ISaveListener iSaveListener, Term term) {
        termsRepository.insert(iSaveListener, term);
    }
    /**
     * This method will:
     * 1. Update a Term
     * @param term
     */
    public void update(ICallbacks.ISaveListener iSaveListener, Term term) {
        termsRepository.update(iSaveListener, term);
    }
    /**
     * This method will:
     * 1. Delete a Term
     * @param term
     */
    public void delete(ICallbacks.ISaveListener iSaveListener, Term term) {
        termsRepository.delete(iSaveListener, term);
    }
    public void fetchAll(ICallbacks.IFetchListener iFetchListener){
        termsRepository.selectAll(iFetchListener);
    }

    public void selectAndPaginate(int start,int limit,ICallbacks.IFetchListener iFetchListener){
        termsRepository.selectAndPaginate(limit, start, iFetchListener);
    }
    public void search(String searchTerm,ICallbacks.IFetchListener iFetchListener){
        termsRepository.search(searchTerm, iFetchListener);
    }
    public void selectByDate(String date,ICallbacks.IFetchListener iFetchListener){
        termsRepository.selectByDate(date, iFetchListener);
    }
}
