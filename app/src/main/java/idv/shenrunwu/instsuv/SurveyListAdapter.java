package idv.shenrunwu.instsuv;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by shenrunwu on 2016/11/13.
 */

public class SurveyListAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;
    private List<StartActivity.SurveyListItem> lists;


    private class ViewHolder {
        TextView creatorTV;
        TextView endTimeTV;
        TextView topicTV;

        public ViewHolder(TextView creator, TextView endTime, TextView topic) {
            this.creatorTV = creator;
            this.endTimeTV = endTime;
            this.topicTV = topic;
        }
    }

    @Override
    public long getItemId(int position) {
        return lists.indexOf(getItem(position));
    }

    @Override
    public Object getItem(int position) {
        return lists.get(position);
    }

    public SurveyListAdapter(Context context, List<StartActivity.SurveyListItem> surveyListItems) {
        layoutInflater = LayoutInflater.from(context);
        this.lists = surveyListItems;
    }

    @Override
    public int getCount() {
        return lists.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        SurveyListAdapter.ViewHolder holder = null;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.survey_list_item, null);
            holder = new SurveyListAdapter.ViewHolder(
                    (TextView) convertView.findViewById(R.id.creatorInSurveyList),
                    (TextView) convertView.findViewById(R.id.endOfSurveyInList),
                    (TextView) convertView.findViewById(R.id.topicInList)
            );
            convertView.setTag(holder);
        } else {
            holder = (SurveyListAdapter.ViewHolder) convertView.getTag();
        }

        StartActivity.SurveyListItem surveyListItem = (StartActivity.SurveyListItem) getItem(position);

        holder.topicTV.setText(surveyListItem.topic);
        holder.creatorTV.setText(surveyListItem.creatorDisplayName);

        Date now = new Date();
        Date date=new Date(surveyListItem.endOfSurveyTime);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        holder.endTimeTV.setText(dateFormat.format(date));
        if(now.getTime()>date.getTime()) {
            holder.endTimeTV.setBackgroundColor(Color.YELLOW);
        }



//        System.out.println(dateFormat.format(date));
        //holder.endTimeTV.setText(surveyListItem.);



        //  *** 尚未完成結束時
        return convertView;
    }
}