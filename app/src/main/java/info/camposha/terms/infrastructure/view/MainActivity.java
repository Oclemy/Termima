package info.camposha.terms.infrastructure.view;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.jhonnyx2012.horizontalpicker.DatePickerListener;
import com.yarolegovich.lovelydialog.LovelyCustomDialog;

import org.joda.time.DateTime;

import java.util.List;
import java.util.Locale;

import easyadapter.dc.com.library.EasyAdapter;
import info.camposha.terms.R;
import info.camposha.terms.databinding.ActivityMainBinding;
import info.camposha.terms.databinding.ModelGridBinding;
import info.camposha.terms.domain.entity.Term;
import info.camposha.terms.domain.usecase.ICallbacks;
import info.camposha.terms.infrastructure.viewmodel.MainViewModel;

public class MainActivity extends AppCompatActivity implements DatePickerListener,
        ICallbacks.ISaveListener, ICallbacks.IFetchListener {

    private ActivityMainBinding b;
    private EasyAdapter<Term, ModelGridBinding> archiveAdapter;
    private EasyAdapter<Term, ModelGridBinding> dialyAdapter;
    private MainViewModel mv;
    private EditText termTxt, meaningTxt;
    private boolean reachedEnd = false;
    private boolean isScrolling;
    private int ITEMS_PER_PAGE = 10;
    private boolean isAfterUpdate = false;

    private void init() {
        this.setupDatePicker();
        this.setupAdapter();
        this.handleEvents();
        this.listenToRecyclerViewScroll();
        this.mv = new ViewModelProvider(this).get(MainViewModel.class);
    }

    public void show(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * This method will:
     * 1. Load our material colors into an array
     * 2. Setup our horizontal datepicker
     */
    private void setupDatePicker() {
        b.datePicker.setListener(this)
                .setDays(360)
                .setOffset(7)
                .setDateSelectedColor(Color.DKGRAY)
                .setDateSelectedTextColor(Color.WHITE)
                .setMonthAndYearTextColor(Color.DKGRAY)
                .setTodayButtonTextColor(getResources().getColor(R.color.colorPrimary))
                .setTodayDateTextColor(getResources().getColor(R.color.colorPrimary))
                .setTodayDateBackgroundColor(Color.GRAY)
                .setUnselectedDayTextColor(Color.DKGRAY)
                .setDayOfWeekTextColor(Color.DKGRAY)
                .setUnselectedDayTextColor(getResources().getColor(R.color.primaryTextColor))
                .showTodayButton(true)
                .init();
        b.datePicker.setBackgroundColor(Color.LTGRAY);
        b.datePicker.setDate(new DateTime());
    }

    /**
     * This method will:
     * 1. setup our adapter for us
     * 2.Setup our recyclerview
     */
    private void setupAdapter() {
        archiveAdapter = new EasyAdapter<Term, ModelGridBinding>(R.layout.model_grid) {
            @Override
            public void onBind(@NonNull ModelGridBinding mb, @NonNull Term t) {
                mb.headerTV.setText(String.format(Locale.getDefault(),"%d. %s", getData().indexOf(t) + 1, t.getTerm()));
                mb.contentTV.setText(t.getMeaning());


                mb.doubleLift.collapse();

                mb.headerTV.setOnClickListener(view -> mb.toggleBtn.performClick());
                mb.toggleBtn.setOnClickListener(view -> {
                    if (mb.doubleLift.isExpanded()) {
                        mb.toggleBtn.setImageResource(R.drawable.ic_keyboard_arrow_down_white_24dp);
                        mb.doubleLift.collapse();
                    } else {
                        mb.toggleBtn.setImageResource(R.drawable.ic_keyboard_arrow_up_white_24dp);
                        mb.doubleLift.expand();
                    }


                });
                mb.editBtn.setOnClickListener(v -> createCustomDialog(true, t));
                mb.deleteBtn.setOnClickListener(v -> mv.delete(MainActivity.this, t));
            }

        };
        GridLayoutManager glm = new GridLayoutManager(this, 1);
        b.rv.setLayoutManager(glm);
        dialyAdapter = archiveAdapter;
        b.rv.setAdapter(dialyAdapter);
//        b.rv.setAdapter(archiveAdapter);

    }

    private void addToAdapter(List<Term> terms, EasyAdapter adapter) {
        List<Term> available = archiveAdapter.getData();
        boolean found = false;
        if (available != null && !available.isEmpty()) {
            for (Term term : terms) {
                for (Term t : available) {
                    if (term.getId() == t.getId()) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    adapter.add(term);
                }
            }
        } else {
            adapter.addAll(terms, true);

        }
        archiveAdapter.notifyDataSetChanged();

    }

    /**
     * This method will:
     * 1. Create our input dialog. We can use this dialog for either update or delete
     *
     * @param forEdit - boolean
     */
    private void createCustomDialog(Boolean forEdit, Term t) {
        Term term = forEdit ? t : new Term();
        LovelyCustomDialog d = new LovelyCustomDialog(this);
        d.setTopColorRes(R.color.colorPrimary)
                .setView(R.layout.dialog_edit)
                .setTitle("Terms Editor")
                .setTitleGravity(Gravity.CENTER_HORIZONTAL)
                .setMessage("Enter term and meaning and click save")
                .setMessageGravity(Gravity.CENTER_HORIZONTAL)
                .setIcon(R.drawable.flip_page)
                .setCancelable(false)
                .configureView(rootView -> {
                    termTxt = rootView.findViewById(R.id.termTxt);
                    meaningTxt = rootView.findViewById(R.id.meaningTxt);
                    Button saveBtn = rootView.findViewById(R.id.saveBtn);

                    saveBtn.setText(forEdit ? "UPDATE" : "ADD");
                    termTxt.setText(forEdit ? term.getTerm() : "");
                    meaningTxt.setText(forEdit ? term.getMeaning() : "");

                    saveBtn.setOnClickListener(view -> {
                        term.setTerm(termTxt.getText().toString());
                        term.setMeaning(meaningTxt.getText().toString());
                        if (forEdit) {
                            mv.update(MainActivity.this, term);
                            d.dismiss();
                        } else {
                            term.setDate(mv.SELECTED_DATE);
                            mv.insert(MainActivity.this, term);
                        }
                    });
                })
                .setListener(R.id.cancelBtn, view -> d.dismiss())
                .show();
    }

    /**
     * When our datepicker is selected, we obtain the selected date and
     * filter our data
     *
     * @param dateSelected - selected date
     */
    @Override
    public void onDateSelected(DateTime dateSelected) {
        archiveAdapter.clear(true);
        String year = String.valueOf(dateSelected.getYear());
        String month = String.valueOf(dateSelected.getMonthOfYear());
        String day = String.valueOf(dateSelected.getDayOfMonth());
        if (dateSelected.getMonthOfYear() < 10) {
            mv.SELECTED_DATE = year + "-0" + month + "-" + day;
        } else {
            mv.SELECTED_DATE = year + "-" + month + "-" + day;
        }
        mv.IS_DAILY_VIEW = true;
        mv.selectByDate(mv.SELECTED_DATE, this);
    }


    /**
     * When our data is saved
     *
     * @param message - success message
     */
    @Override
    public void onSuccess(String message) {
        isAfterUpdate = true;
        if (termTxt != null && meaningTxt != null) {
            termTxt.setText("");
            meaningTxt.setText("");
        }
        show(message);
        archiveAdapter.clear(true);
    }

    /**
     * When an error occurs
     *
     * @param error - error message
     */
    @Override
    public void onError(String error) {
        show(error);
    }

    /**
     * When our data is loaded
     *
     * @param terms - list of terms
     */
    @Override
    public void onDataFetched(List<Term> terms) {
        if (!mv.IS_DAILY_VIEW) {
            if (isAfterUpdate) {
                archiveAdapter.notifyDataSetChanged();
            } else {
                addToAdapter(terms, archiveAdapter);
            }
            if (terms.size() > 0) {
                reachedEnd = false;
            } else {
                if (!reachedEnd) {
                    show("No More data. Reached End");
                }
                reachedEnd = true;
            }
            archiveAdapter.notifyDataSetChanged();
        } else {
            dialyAdapter.clear(true);
            addToAdapter(terms, dialyAdapter);
        }
        isAfterUpdate = false;

    }

    /**
     * We will listen to scroll events. This is important as we are implementing scroll to
     * load more data pagination technique
     */
    private void listenToRecyclerViewScroll() {
        b.rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView rv, int newState) {
                //when scrolling starts
                super.onScrollStateChanged(rv, newState);
                //check for scroll state
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true;
                }
            }

            @Override
            public void onScrolled(RecyclerView rv, int dx, int dy) {
                // When the scrolling has stopped
                super.onScrolled(rv, dx, dy);

                //getChildCount() returns the current number of child views attached to the
                // parent RecyclerView.
                int current = b.rv.getLayoutManager().getChildCount();
                //getItemCount() returns the number of items in the adapter bound to the
                // parent RecyclerView.
                int total = b.rv.getLayoutManager().getItemCount();
                //findFirstVisibleItemPosition() returns the adapter position of the first
                // visible view.
                int scrolledOut = ((LinearLayoutManager) b.rv.getLayoutManager()).findFirstVisibleItemPosition();

                if (isScrolling && (current + scrolledOut == total)) {
                    isScrolling = false;

                    if (dy > 0) {
                        // Scrolling up
                        if (!reachedEnd && !mv.IS_DAILY_VIEW) {
                            mv.selectAndPaginate(b.rv.getLayoutManager().getItemCount(), ITEMS_PER_PAGE, MainActivity.this);
                        }
                    }
                }
            }
        });
    }

    /**
     * Let's handle our events
     */
    private void handleEvents() {
        b.addBtn.setOnClickListener(view -> createCustomDialog(false, new Term()));
        b.switchBtn.setOnClickListener(view -> {
            mv.IS_DAILY_VIEW = !mv.IS_DAILY_VIEW;

            // setupAdapter();
            archiveAdapter.clear(true);

            if (mv.IS_DAILY_VIEW) {
                b.rv.setAdapter(dialyAdapter);
                mv.selectByDate(mv.SELECTED_DATE, this);
                b.datePicker.setVisibility(View.VISIBLE);
                b.searchViewTxt.setVisibility(View.GONE);
            } else {
                b.rv.setAdapter(archiveAdapter);
                mv.selectAndPaginate(0, ITEMS_PER_PAGE, this);
                b.datePicker.setVisibility(View.GONE);
                b.searchViewTxt.setVisibility(View.VISIBLE);

            }
        });
        b.refreshBtn.setOnClickListener(view -> {
            if (mv.IS_DAILY_VIEW) {
                mv.selectByDate(mv.SELECTED_DATE, this);
            } else {
                mv.selectAndPaginate(0, ITEMS_PER_PAGE, MainActivity.this);
            }
        });
        b.searchViewTxt.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                archiveAdapter.clear(true);
                mv.search(query, MainActivity.this);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                archiveAdapter.clear(true);
                mv.search(newText, MainActivity.this);
                return false;
            }
        });
    }

    /**
     * Our onCreate callback
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = DataBindingUtil.setContentView(this, R.layout.activity_main);

        this.init();

    }
}
