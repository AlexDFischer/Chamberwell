package me.alexfischer.maxwellsdemon;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

public class GameActivity extends AppCompatActivity
{
    private GameView gameView;
    private int level;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Log.d("adf", "gameFrameLayout size is " + findViewById(R.id.gameFrameLayout).getWidth() + " X " + findViewById(R.id.gameFrameLayout).getHeight());
        Intent intent = getIntent();
        this.level = intent.getIntExtra("level", -1);
        setTitle(getString(R.string.game_activity_title).replace("*", Integer.toString(level)));
        FrameLayout gameFrame = (FrameLayout)(findViewById(R.id.gameFrameLayout));
        gameView = new GameView(this);
        gameFrame.addView(gameView);

        /*
        gameView.setGameController(
                new GameController()
                {
                    private double doorPosition;
                    Paint paint = new Paint();

                    @Override
                    public boolean isVictorious()
                    {
                        return false;
                    }

                    @Override
                    public void update()
                    {

                    }

                    @Override
                    public double getDoorPosition()
                    {
                        return doorPosition;
                    }

                    @Override
                    public void setDoorPosition(double pos)
                    {
                        this.doorPosition = pos;
                        //Log.d("adf", "doorPosition is " + pos);
                    }

                    @Override
                    public void paint(Canvas canvas)
                    {
                        paint.setARGB(128, 0, 0, 0);
                        paint.setTextSize(50.0f);
                        canvas.drawText("doorPosition is " + doorPosition, 0.0f, 100.0f, paint);
                        //Log.d("adf", "painting GameController with doorPosition = " + doorPosition);
                    }
                }
        );
        */

        gameView.setGameController(new GameControllerImpl(
                intent.getIntExtra("numRedBalls", 0),
                intent.getIntExtra("numPurpleBalls", 0),
                intent.getIntExtra("numBlueBalls", 0),
                intent.getStringExtra("chambers"),
                intent.getFloatExtra("doorWidth", 0.5f)
                ));
    }

    @Override
    public void onStart()
    {
        super.onStart();
        gameView.start();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        gameView.pause();
    }

    public void onSuccess(View view)
    {
        Intent data = new Intent();
        data.putExtra("level", level);
        setResult(Static.LEVEL_STATUS_SUCCESS, data);
        gameView.stop();
        finish();
    }

    public void onFailure(View view)
    {
        Intent data = new Intent();
        data.putExtra("level", level);
        setResult(Static.LEVEL_STATUS_FAILURE, data);
        gameView.stop();
        finish();
    }
}
