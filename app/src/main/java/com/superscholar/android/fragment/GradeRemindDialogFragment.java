package com.superscholar.android.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import com.superscholar.android.R;
import com.superscholar.android.entity.User;
import com.superscholar.android.tools.ServerConnector;
import com.superscholar.android.tools.UserLib;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by zhqy on 2017/9/17.
 */

public class GradeRemindDialogFragment extends DialogFragment {
    private RadioButton onButton;
    private RadioButton offButton;
    private boolean isFinish=false;
    private String message;


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view= LayoutInflater.from(getActivity()).inflate(R.layout.dialog_grade_remind,null);
        onButton=(RadioButton) view.findViewById(R.id.grade_remind_button_on);
        offButton=(RadioButton) view.findViewById(R.id.grade_remind_button_off);
        final User user= UserLib.getInstance().getUser();
        if(user.isRemind()){
            onButton.setChecked(true);
        }else{
            offButton.setChecked(true);
        }

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle("成绩提醒")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boolean old=user.isRemind();
                        final boolean now=onButton.isChecked();
                        if(old!=now){
                            final ProgressDialog pd=new ProgressDialog(getActivity());
                            pd.setTitle("成绩提醒");
                            pd.setMessage("设置中，请稍候...");
                            pd.setCancelable(false);
                            pd.show();
                            ServerConnector.getInstance().setUserRemind(user.getUsername(), now, new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            message="网络异常，请检查网络设置";
                                            isFinish=true;
                                        }
                                    });
                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    String resp=response.body().string();
                                    try {
                                        JSONObject jsonObject=new JSONObject(resp);
                                        int code=jsonObject.getInt("code");
                                        if(code!=1){
                                            message=jsonObject.getString("message");
                                            isFinish=true;
                                        }else{
                                            UserLib.getInstance().getUser().setRemind(now);
                                            message="设置成功";
                                            isFinish=true;
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        message="发生异常";
                                        isFinish=true;
                                    }
                                }
                            });
                            while(!isFinish){

                            }
                            pd.dismiss();
                            Toast.makeText(getActivity(),message,Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("取消",null)
                .create();
    }
}
