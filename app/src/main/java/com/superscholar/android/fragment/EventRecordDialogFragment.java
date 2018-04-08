package com.superscholar.android.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.superscholar.android.R;
import com.superscholar.android.entity.EventItem;
import com.superscholar.android.tools.Date;
import com.superscholar.android.tools.EventBaseManager;
import com.superscholar.android.tools.Time;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by zhqy on 2017/9/5.
 */

public class EventRecordDialogFragment extends DialogFragment {
    private EditText editText;

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view= LayoutInflater.from(getActivity()).inflate(R.layout.dialog_event_record,null);
        final Bundle arguements=getArguments();
        final String type=arguements.getString("type");

        Date startDate=arguements.getParcelable("startDate");
        Date endDate=arguements.getParcelable("endDate");
        Time startTime=arguements.getParcelable("startTime");
        Time endTime=arguements.getParcelable("endTime");

        int time=(int)((endDate.getTimestamp()+endTime.getTimestamp()-startDate.getTimestamp()-startTime.getTimestamp())/(60*1000));
        TextView textView=(TextView)view.findViewById(R.id.dialog_event_record_hint);
        textView.setText("已记录【"+type+"】"+time+"分钟，请输入事件名");
        editText=(EditText) view.findViewById(R.id.dialog_event_record_editText);
        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle("事件记录")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name=editText.getText().toString();
                        if(name.equals("")){
                            name=type;
                        }

                        if(getTargetFragment()==null){
                            return;
                        }

                        Date startDate=arguements.getParcelable("startDate");
                        Date endDate=arguements.getParcelable("endDate");
                        Time startTime=arguements.getParcelable("startTime");
                        Time endTime=arguements.getParcelable("endTime");

                        EventBaseManager manager=new EventBaseManager(getActivity().getApplicationContext());

                        Date date=(Date)startDate.clone();
                        while(!date.isEquals(endDate)){
                            EventItem item=new EventItem(UUID.randomUUID(),date,startTime,new Time(24,00),type,name);
                            manager.insertItem(item);
                            startTime=new Time(0,0);
                            date.dayAddOne();
                        }
                        if(startTime.getTimeDifference(endTime)>0){
                            EventItem item=new EventItem(UUID.randomUUID(),date,startTime,endTime,type,name);
                            manager.insertItem(item);
                        }

                        Toast.makeText(getActivity(),"记录成功",Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("取消",null)
                .create();
    }
}
