package info.camposha.terms.domain.usecase;

import java.util.List;

import info.camposha.terms.domain.entity.Term;

public class ICallbacks {
    /**
     * Our CRUD Methods
     */
    public interface ICrud {
        void insert(final ISaveListener iSaveListener, final Term term);
        void update(final ISaveListener iSaveListener, final Term term);
        void delete(final ISaveListener iSaveListener, final Term term);
        void selectAll(final IFetchListener iFetchListener);
        void selectAndPaginate(int limit,int start,final IFetchListener iFetchListener);
        void search(String searchTerm,final IFetchListener iFetchListener);
        void selectByDate(String searchTerm,final IFetchListener iFetchListener);
    }
    /**
     * Our DataSave Listener
     * Will be raised once our data is saved.
     */
    public interface ISaveListener {
        void onSuccess(String message);
        void onError(String error);
    }

    /**
     * Our DataLoad Listener
     * Will be raised once our data is loaded. A List of loaded items will
     * passed to us
     */
    public interface IFetchListener {
        void onDataFetched(List<Term> terms);
    }
}
