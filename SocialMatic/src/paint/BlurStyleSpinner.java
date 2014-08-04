package paint;

import com.kooco.socialmatic.camera.EditPictureView;

import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.widget.Spinner;

public class BlurStyleSpinner extends Spinner {

	public BlurStyleSpinner(Context context) {
		super(context);
	}

	public BlurStyleSpinner(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public BlurStyleSpinner(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		EditPictureView painter = (EditPictureView) getContext();
		painter.resetPresets();
		super.onClick(dialog, which);
	}
}