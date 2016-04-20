package webinar.pubnub.insitu.fragments;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import webinar.pubnub.insitu.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ManageDiariesFragment.OnManageDiariesInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ManageDiariesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ManageDiariesFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    @Bind(R.id.create_diary_fab)
    FloatingActionButton createDiaryButton;
    @Bind(R.id.add_symptoms_fab)
    FloatingActionButton addSymptomsButton;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OnManageDiariesInteractionListener mListener;
    @Bind(R.id.menu_manage_diaries)
    FloatingActionMenu manageDiariesActionMenu;

    public ManageDiariesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ManageDiariesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ManageDiariesFragment newInstance(String param1, String param2) {
        ManageDiariesFragment fragment = new ManageDiariesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
//
//    @OnClick(R.id.menu_manage_diaries)
//    void toggleMenu() {
//        if (manageDiariesActionMenu.isOpened()) {
//            Toast.makeText(getActivity(), manageDiariesActionMenu.getMenuButtonLabelText(), Toast.LENGTH_SHORT).show();
//        }
//
//        manageDiariesActionMenu.toggle(true);
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_manage_diaries, container, false);
//        ButterKnife.bind(this, view);
//        setupFloatingMenu();
//

        return view;
    }



    private void setupFloatingMenu() {
        manageDiariesActionMenu.setClosedOnTouchOutside(true);
        manageDiariesActionMenu.hideMenuButton(false);
        manageDiariesActionMenu.setOnMenuButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (manageDiariesActionMenu.isOpened()) {
                    Toast.makeText(getActivity(), manageDiariesActionMenu.getMenuButtonLabelText(), Toast.LENGTH_SHORT).show();
                }

                manageDiariesActionMenu.toggle(true);
            }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.OnManageDiariesInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnManageDiariesInteractionListener) {
            mListener = (OnManageDiariesInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnManageDiariesInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnManageDiariesInteractionListener {
        // TODO: Update argument type and name
        void OnManageDiariesInteraction(Uri uri);
    }
}
