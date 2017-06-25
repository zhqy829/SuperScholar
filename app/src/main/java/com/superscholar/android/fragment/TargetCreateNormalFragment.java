package com.superscholar.android.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.superscholar.android.R;
import com.superscholar.android.entity.TargetItem;
import com.superscholar.android.tools.LogUtil;

import java.util.Calendar;
import java.util.Date;

import static android.app.Activity.RESULT_OK;

/**
 * Created by zhqy on 2017/6/24.
 */

public class TargetCreateNormalFragment extends Fragment{

    private boolean isUpdate=false;
    private TargetItem targetItem;

    private EditText targetNameEdit;
    private EditText weekEdit;
    private EditText timesEdit;
    private RadioButton yesButton;
    private RadioButton noButton;
    private TimePicker timePicker;
    private EditText currencyEdit;
    private TextView hintText;
    private Button createButton;

    //变量初始化
    private void initVariable(View view){
        targetNameEdit=(EditText)view.findViewById(R.id.target_create_normal_targetName);
        weekEdit=(EditText)view.findViewById(R.id.target_create_normal_weekEdit);
        timesEdit=(EditText)view.findViewById(R.id.target_create_normal_timesEdit);
        timePicker=(TimePicker)view.findViewById(R.id.target_create_normal_timePicker);
        yesButton=(RadioButton)view.findViewById(R.id.target_create_normal_yesButton);
        noButton=(RadioButton)view.findViewById(R.id.target_create_normal_noButton);
        currencyEdit=(EditText)view.findViewById(R.id.target_create_normal_currencyEdit);
        hintText=(TextView)view.findViewById(R.id.target_create_normal_hintText);
        createButton=(Button)view.findViewById(R.id.target_create_normal_createButton);
    }

    //时间选择器初始化
    private void initTimePicker(){
        timePicker.setIs24HourView(true);
        timePicker.setCurrentHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
    }

    //单选框初始化
    private void initRadioGroup(View view){
        yesButton.setChecked(true);
        RadioGroup radioGroup=(RadioGroup)view.findViewById(R.id.target_create_normal_radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(yesButton.isChecked()){
                    timePicker.setEnabled(true);
                }
                else{
                    timePicker.setEnabled(false);
                }
            }
        });
    }

    //添加周数编辑框输入监听
    private void initWeekEditListener(){
        weekEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String weekString=weekEdit.getText().toString();
                if(weekString.equals("")){
                    hintText.setText("");
                    return;
                }
                if(Integer.parseInt(weekString)>10){
                    Toast.makeText(getActivity(),"周数不能大于10",Toast.LENGTH_SHORT).show();
                    weekEdit.setText("10");
                    return;
                }
                if(Integer.parseInt(weekString)==0){
                    Toast.makeText(getActivity(),"周数不能为0",Toast.LENGTH_SHORT).show();
                    weekEdit.setText("1");
                    return;
                }
                String timesString=timesEdit.getText().toString();
                if(timesString.equals("")){
                    hintText.setText("");
                    return;
                }else{
                    int week=Integer.parseInt(weekString);
                    int times=Integer.parseInt(timesString);
                    int grade=2*week*times;
                    hintText.setText("目标达成后您将获得"+grade+"学分绩");
                }
            }
        });
    }

    //添加次数编辑框输入监听
    private void initTimesEditListener(){
        timesEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String timesString=timesEdit.getText().toString();
                if(timesString.equals("")){
                    hintText.setText("");
                    return;
                }
                if(Integer.parseInt(timesString)>7){
                    Toast.makeText(getActivity(),"次数不能大于7",Toast.LENGTH_SHORT).show();
                    timesEdit.setText("7");
                    return;
                }
                if(Integer.parseInt(timesString)==0){
                    Toast.makeText(getActivity(),"次数不能为0",Toast.LENGTH_SHORT).show();
                    timesEdit.setText("1");
                    return;
                }
                String weekString=weekEdit.getText().toString();
                if(weekString.equals("")){
                    hintText.setText("");
                    return;
                }else{
                    int week=Integer.parseInt(weekString);
                    int times=Integer.parseInt(timesString);
                    int grade=2*week*times;
                    hintText.setText("目标达成后您将获得"+grade+"学分绩");
                }
            }
        });
    }

    //按钮初始化
    private void initButton(View view){
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isUpdate){
                    if(updateTarget()){
                        Intent intent=new Intent();
                        intent.putExtra("targetUpdate",targetItem);
                        getActivity().setResult(RESULT_OK,intent);
                        getActivity().finish();
                    }
                }else{
                    if(createTarget()){
                        Intent intent=new Intent();
                        intent.putExtra("targetCreate",targetItem);
                        getActivity().setResult(RESULT_OK,intent);
                        getActivity().finish();
                    }
                }
            }
        });
    }

    //更新目标
    private boolean updateTarget(){
        String currencyString=currencyEdit.getText().toString();
        if(currencyString.equals("")){
            Toast.makeText(getActivity(),"奖励币不能为空",Toast.LENGTH_SHORT).show();
            return false;
        }
        int currencyReward=Integer.parseInt(currencyString);
        if(currencyReward==0){
            Toast.makeText(getActivity(),"奖励币不能为0",Toast.LENGTH_SHORT).show();
            return false;
        }
        targetItem.setCurrencyReward(currencyReward);

        if(yesButton.isChecked()) {
            targetItem.setNeedRemind(true);
            int hour=timePicker.getCurrentHour();
            int min=timePicker.getCurrentMinute();
            targetItem.getRemindTime().setHour(hour);
            targetItem.getRemindTime().setMin(min);
        }else{
            targetItem.setNeedRemind(false);
        }
        return true;
    }

    //创建目标
    private boolean createTarget(){
        String targetName=targetNameEdit.getText().toString();
        if(targetName.equals("")){
            Toast.makeText(getActivity(),"目标名不能为空",Toast.LENGTH_SHORT).show();
            return false;
        }

        String weekString=weekEdit.getText().toString();
        String timesString=timesEdit.getText().toString();
        if(weekString.equals("")||timesString.equals("")){
            Toast.makeText(getActivity(),"周数和次数不能为空",Toast.LENGTH_SHORT).show();
            return false;
        }

        String currencyString=currencyEdit.getText().toString();
        if(currencyString.equals("")){
            Toast.makeText(getActivity(),"奖励币不能为空",Toast.LENGTH_SHORT).show();
            return false;
        }
        int currencyReward=Integer.parseInt(currencyString);
        if(currencyReward==0){
            Toast.makeText(getActivity(),"奖励币不能为0",Toast.LENGTH_SHORT).show();
            return false;
        }

        if(yesButton.isChecked()){
            int hour,min;
            hour= timePicker.getCurrentHour();
            min=timePicker.getCurrentMinute();
            targetItem=new TargetItem(targetName,Integer.parseInt(timesString),Integer.parseInt(weekString),
                    false,hour,min,
                    new com.superscholar.android.tools.Date(),currencyReward);
        }else{
            targetItem=new TargetItem(targetName,Integer.parseInt(timesString),Integer.parseInt(weekString),
                    new com.superscholar.android.tools.Date(),currencyReward);
        }
        return true;
    }

    //更新初始化
    private void updateInit(){
        targetNameEdit.setText(targetItem.getName());
        targetNameEdit.setEnabled(false);

        weekEdit.setText(String.valueOf(targetItem.getLastedWeek()));
        weekEdit.setEnabled(false);

        timesEdit.setText(String.valueOf(targetItem.getTimesPerWeek()));
        timesEdit.setEnabled(false);

        if(targetItem.isNeedRemind()){
            yesButton.setChecked(true);
            timePicker.setCurrentHour(targetItem.getRemindTime().getHour());
            timePicker.setCurrentMinute(targetItem.getRemindTime().getMin());
        }else{
            noButton.setChecked(true);
        }
        currencyEdit.setText(String.valueOf(targetItem.getCurrencyReward()));
        currencyEdit.requestFocus();

        createButton.setText("修   改");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args=getArguments();
        if(args==null) return;
        else{
            isUpdate=getArguments().getBoolean("isUpdate",false);
        }
        if(isUpdate){
            targetItem=getArguments().getParcelable("targetItem");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.target_create_normal,container,false);


        initVariable(view);
        initWeekEditListener();
        initTimesEditListener();
        initTimePicker();
        initRadioGroup(view);
        initButton(view);

        if(isUpdate){
            updateInit();
        }

        return view;
    }
}
