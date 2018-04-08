package com.superscholar.android.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.superscholar.android.R;

/**
 * Created by zhqy on 2017/6/24.
 */

public class ConcentrationSettingDialogFragment extends DialogFragment{

    private EditText editText;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view= LayoutInflater.from(getActivity()).inflate(R.layout.dialog_concentration_setting,null);
        int currentCurrencyBase=getArguments().getInt("currencyBase",0);
        TextView textView=(TextView)view.findViewById(R.id.dialog_concentration_setting_textView);
        textView.setText("当前基数："+currentCurrencyBase);
        editText=(EditText) view.findViewById(R.id.dialog_concentration_setting_editText);
        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle("设置奖励币")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String text=editText.getText().toString();
                        if(text.equals("")){
                            Toast.makeText(getActivity(),"请输入基数",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        int currencyBase=Integer.parseInt(text);
                        if(getTargetFragment()==null){
                            return;
                        }
                        Intent intent=new Intent();
                        intent.putExtra("currencyBase",currencyBase);
                        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK,intent);
                    }
                })
                .create();
    }
}
