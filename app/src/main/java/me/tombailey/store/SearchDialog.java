package me.tombailey.store;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

/**
 * Created by tomba on 11/03/2017.
 */

public class SearchDialog extends AlertDialog {

    protected SearchDialog(Context context) {
        super(context);
    }

    public static class Builder extends AlertDialog.Builder {

        private SearchListener mSearchListener;

        private String mTitle;

        private String mSearch;
        private String mCancel;

        private String mSearchQueryRequired;

        private View mSearchView;

        public Builder(Context context) {
            super(context);

            mTitle = context.getString(R.string.search_dialog_title);

            mSearch = context.getString(R.string.search);
            mCancel = context.getString(R.string.cancel);

            mSearchQueryRequired = context.getString(R.string.search_dialog_keywords_required);

            mSearchView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(R.layout.search_dialog_view, null);
        }

        public Builder searchClicked(SearchListener searchListener) {
            mSearchListener = searchListener;
            return this;
        }

        @Override
        public AlertDialog create() {
            setView(mSearchView);
            setTitle(mTitle);

            //default positive button behaviour closes the AlertDialog. To avoid this, when
            //bad input is provided, the on click listener is overridden when the dialog is shown.
            //See alertDialog.setOnShowListener() below
            setPositiveButton(mSearch, null);
            setNegativeButton(mCancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            setCancelable(false);

            final AlertDialog alertDialog = super.create();
            alertDialog.setOnShowListener(new OnShowListener() {
                @Override
                public void onShow(final DialogInterface dialog) {
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            EditText etKeywords = (EditText) mSearchView.findViewById(R.id.search_dialog_edit_text_keywords);
                            String keywords = etKeywords.getText().toString();

                            if (keywords.trim().length() > 0) {
                                dialog.dismiss();
                                mSearchListener.onSearch(keywords);
                            } else {
                                etKeywords.setError(mSearchQueryRequired);
                            }
                        }
                    });
                }
            });
            return alertDialog;
        }
    }

    public interface SearchListener {
        void onSearch(String keywords);
    }
}
