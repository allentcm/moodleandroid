package app.moodle.moodle.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import app.moodle.moodle.R;
import app.moodle.moodle.models.Course;


public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.ViewHolder> {

    private Context mContext;
    private List<Course> mCourses;
    private ItemInteractionListener mItemInteractionListener;

    public static class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener {

        private ItemInteractionListener mListener;
        TextView mFullname;
        TextView mSummary;

        public ViewHolder(View itemView, ItemInteractionListener listener) {
            super(itemView);
            mListener = listener;
            mFullname = (TextView) itemView.findViewById(R.id.courseTitle);
            mSummary = (TextView) itemView.findViewById(R.id.courseSummary);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mListener != null)
                mListener.onItemClick(v, getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            if (mListener != null)
                mListener.onItemLongClick(v, getAdapterPosition());
            return true;
        }
    }

    public CourseAdapter(Context context) {
        mContext = context;
        mCourses = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.list_item_course, parent, false);
        return new ViewHolder(view, mItemInteractionListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Course course = mCourses.get(position);

        holder.mFullname.setText(course.getFullname());
        holder.mSummary.setText(course.getSummary());
    }

    @Override
    public int getItemCount() {
        return mCourses.size();
    }

    public Course getItem(int position) {
        return mCourses.get(position);
    }

    public void updateCourses(List<Course> courses) {
        notifyItemInserted(getItemCount());
        mCourses = courses;
    }


    /**
     * Interface for listeners to listen to ViewHolder items interaction
     */
    public interface ItemInteractionListener {
        /**
         * Adapter's item was clicked by user
         *
         * @param view of the item
         * @param position of the item in adapter
         */
        void onItemClick(View view, int position);

        /**
         * User performs long click on the adapter's item
         *
         * @param view of the item
         * @param position of the item in adapter
         * @return true if the callback consumed the long click, false otherwise
         */
        boolean onItemLongClick(View view, int position);

        /**
         * User click on the adapter's item more button (if applicable)
         *
         * @param view of the item
         * @param position of the item in adapter
         */
        void onMoreButtonClick(View view, int position);
    }

    /**
     * Convenient method to set the ItemInteractionListener
     *
     * @param listener to listen to ViewHolder items interaction
     */
    public void setOnItemInteractionListener(ItemInteractionListener listener) {
        mItemInteractionListener = listener;
    }
}
