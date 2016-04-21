package webinar.pubnub.insitu.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;

import butterknife.ButterKnife;
import webinar.pubnub.insitu.R;
import webinar.pubnub.insitu.managers.SymptomManager;
import webinar.pubnub.insitu.model.Symptom;


/**
 * Created by Konstantinos Michail on 4/20/2016.
 */
public class MoreInfoDialog extends DialogFragment {
    public static MoreInfoDialog newInstance(int symptomId) {
        MoreInfoDialog fragment = new MoreInfoDialog();
        Bundle args = new Bundle();
        args.putInt("id",symptomId);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int id = getArguments().getInt("id");

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.more_info_dialog, null);
        ButterKnife.bind(this, view);

        Symptom symptom= SymptomManager.getInstance().getSymptomById(id);

        return new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.more_info_title))
                .setMessage(getString(R.string.more_info_message))
                .setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .create();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }
}
