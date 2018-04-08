package com.superscholar.android.tools;

import com.superscholar.android.entity.StorableSubjectBean;
import com.zhuangfei.timetable.core.SubjectBean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhqy
 * @date 2018/4/8
 */

public class SubjectBeanConverter {
    public static StorableSubjectBean toStorable(SubjectBean bean){
        StorableSubjectBean storable = new StorableSubjectBean();
        storable.setName(bean.getName());
        storable.setClassroom(bean.getRoom());
        storable.setTeacher(bean.getTeacher());
        storable.setDayOfWeek(bean.getDay());
        storable.setTimeStart(bean.getStart());
        storable.setTimeEnd(bean.getStart() + bean.getStep() - 1);
        storable.setWeekStart(bean.getWeekList().get(0));
        storable.setWeekEnd(bean.getWeekList().get(bean.getWeekList().size() - 1));
        return storable;
    }

    public static SubjectBean toReal(StorableSubjectBean bean){
        SubjectBean real = new SubjectBean();
        real.setName(bean.getName());
        real.setRoom(bean.getClassroom());
        real.setTeacher(bean.getTeacher());
        real.setDay(bean.getDayOfWeek());
        real.setStart(bean.getTimeStart());
        real.setStep(bean.getTimeEnd() - bean.getTimeStart() + 1);
        List<Integer> weekList = new ArrayList<>();
        for(int i = bean.getWeekStart();i <= bean.getWeekEnd();i++){
            weekList.add(i);
        }
        real.setWeekList(weekList);
        real.setColorRandom(bean.getName().hashCode() % 8);
        return real;
    }
}
