package example.swa.yesnogame.ui;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;

/**
 * ArrayAdapter which replaces the default ArrayAdapter. Changes the getView
 * method to replace background colors on the last selected item.
 * 
 * @author Hendrik.Stilke@siemens.com
 * 
 * @param <T>
 */
public class YesNoArrayAdapter<T> extends ArrayAdapter<T> {

	private int selectionIndex = Adapter.NO_SELECTION;

	public YesNoArrayAdapter(Context context, int resource, List<T> objects) {
		super(context, resource, objects);
	}

	public int getSelectionIndex() {
		return this.selectionIndex;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final View renderer = super.getView(position, convertView, parent);
		if (position == this.selectionIndex) {
			renderer.setBackgroundResource(android.R.color.darker_gray);
		} else {
			renderer.setBackgroundResource(android.R.color.transparent);
		}
		return renderer;
	}

	public void setSelectionIndex(int index) {
		this.selectionIndex = index;
	}
}
