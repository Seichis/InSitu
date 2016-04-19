package webinar.pubnub.insitu.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import webinar.pubnub.insitu.R;
import webinar.pubnub.insitu.dragNdrop.CoolDragAndDropGridView;
import webinar.pubnub.insitu.dragNdrop.DiaryItem;
import webinar.pubnub.insitu.dragNdrop.DiaryItemAdapter;
import webinar.pubnub.insitu.dragNdrop.SimpleScrollingStrategy;
import webinar.pubnub.insitu.dragNdrop.SpanVariableGridView;
import webinar.pubnub.insitu.managers.DiaryManager;
import webinar.pubnub.insitu.managers.SymptomManager;
import webinar.pubnub.insitu.model.Diary;
import webinar.pubnub.insitu.model.Symptom;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnDiaryFragmentListener} interface
 * to handle interaction events.
 * Use the {@link DiaryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DiaryFragment extends Fragment implements CoolDragAndDropGridView.DragAndDropListener, SpanVariableGridView.OnItemClickListener,
        SpanVariableGridView.OnItemLongClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "DiaryFragment";
    public List<Diary> diaries = new ArrayList<>();
    DiaryItemAdapter mItemAdapter;
    DiaryManager diaryManager;
    //    CoolDragAndDropGridView mCoolDragAndDropGridView;
    List<DiaryItem> mItems = new LinkedList<>();
    @Bind(R.id.create_diary_button)
    Button createDiaryButton;
    @Bind(R.id.scrollView)
    ScrollView scrollView;
    @Bind(R.id.coolDragAndDropGridViewDiary)
    CoolDragAndDropGridView mCoolDragAndDropGridView;

    @Bind(R.id.input_diary_name)
    EditText inputDiaryNameEditText;

    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;
    private OnDiaryFragmentListener mListener;

    public DiaryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DiaryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DiaryFragment newInstance(String param1, String param2) {
        DiaryFragment fragment = new DiaryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @OnClick(R.id.create_diary_button)
    void createDiary() {
        String input = inputDiaryNameEditText.getText().toString();
        Diary sameNameDiary = DiaryManager.getInstance().searchByName(inputDiaryNameEditText.getText().toString());
        Log.i(TAG, "same name diaries " + sameNameDiary);
        // If the input field is not empty continue
        if (input.isEmpty()) {
            Toast.makeText(getContext(), "Please add a name for the diary you want to create", Toast.LENGTH_LONG).show();
            return;
        }

        if (sameNameDiary!=null) {
            Toast.makeText(getContext(), "A diary with the same name exists. Choose another name", Toast.LENGTH_LONG).show();
        } else {

            diaryManager.createDiary(input, "This is the description : Monitor something");
            setupDiaries();

        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        diaryManager = DiaryManager.getInstance();

        setupDiaries();
    }

    private void setupDiaries() {
        diaries = diaryManager.getDiaries();
        if (!diaries.isEmpty()) {
            for (Diary d : diaries) {
                mItems.add(new DiaryItem(R.drawable.common_google_signin_btn_icon_dark_focused, 3, d.getName(), d.getDescription()));
                Log.i(TAG, "name" + d.getName());
                Log.i(TAG, "description" + d.getDescription());

                TreeMap<Integer, String> symTypes = d.getSymptomTypes();
                Log.i(TAG, String.valueOf(symTypes));
                HashMap<String, List<Symptom>> symptomsMap = SymptomManager.getInstance().getSymptomsByDiary(d);
                for (List<Symptom> slist : symptomsMap.values()) {
                    for (Symptom s : slist) {
                        //TODO log
                    }
                }
            }
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_diary, container, false);
        ButterKnife.bind(this, view);

        mItemAdapter = new DiaryItemAdapter(getContext(), mItems);
        mCoolDragAndDropGridView.setAdapter(mItemAdapter);
        mCoolDragAndDropGridView.setScrollingStrategy(new SimpleScrollingStrategy(scrollView));
        mCoolDragAndDropGridView.setDragAndDropListener(this);
        mCoolDragAndDropGridView.setOnItemLongClickListener(this);
        mItemAdapter.notifyDataSetChanged();

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onDiaryFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnDiaryFragmentListener) {
            mListener = (OnDiaryFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnDiaryFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

        mCoolDragAndDropGridView.startDragAndDrop();

        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

    }

    @Override
    public void onDragItem(int from) {

    }

    @Override
    public void onDraggingItem(int from, int to) {

    }

    @Override
    public void onDropItem(int from, int to) {
        if (from != to) {
            Log.i(TAG, "on drop from " + from + " to " + to);
            mItems.add(to, mItems.remove(from));
            mItemAdapter.notifyDataSetChanged();
        }
        if (to == 0) {
            Log.i(TAG, "on drop from " + diaries.get(from).getName() + " to " + diaries.get(to).getName());
            Collections.swap(diaries, to, from);
        }
    }

    @Override
    public boolean isDragAndDropEnabled(int position) {
        return true;
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnDiaryFragmentListener {
        // TODO: Update argument type and name
        void onDiaryFragmentInteraction(Uri uri);
    }
}
