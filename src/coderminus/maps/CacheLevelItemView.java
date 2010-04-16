package coderminus.maps;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CacheLevelItemView extends LinearLayout 
{

	private TextView  labelTextView;
	private ImageView iconImageView;
	public CacheLevelItemView(Context context) 
	{
		super(context);
		inflate(context, R.layout.cache_level_item_layout, this);
		labelTextView = (TextView ) findViewById(R.id.labelTextView);
		labelTextView.setTextColor(Color.DKGRAY);
		iconImageView = (ImageView) findViewById(R.id.iconImageView);
 		
	}

	public void setData(String text, Bitmap icon) 
	{
		this.labelTextView.setText(text);
		this.iconImageView.setImageBitmap(icon);
	}

}
