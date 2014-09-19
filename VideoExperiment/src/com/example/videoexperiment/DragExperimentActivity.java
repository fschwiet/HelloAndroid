package com.example.videoexperiment;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipDescription;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.DragShadowBuilder;

public class DragExperimentActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_drag_experiment);
		
		final View dragButton = (View)findViewById(R.id.experiment_draggable);
		
		dragButton.setLongClickable(true);
		dragButton.setOnLongClickListener(new View.OnLongClickListener(){

			@Override
			public boolean onLongClick(View v) {
				
				ClipData dragData = ClipData.newPlainText(INPUT_SERVICE, "hi");
				View.DragShadowBuilder myShadow = new DragShadowBuilder(dragButton);

				v.startDrag(dragData,  // the data to be dragged
					myShadow,  // the drag shadow builder
					null,      // no need to use local data
					0          // flags (not currently used, set to 0)
				);
    
				return true;
			}
		});
	}
}
