package me.tanwang.cuelift;

import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class LiftListActivity extends AppCompatActivity implements LiftListFragment.LiftLiftFragmentCallbacks {

    private static final String TAG = "LiftListActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lift_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab == null) {
            return;
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "This brings up a pager with a fragment for the new lift", Toast.LENGTH_SHORT).show();
                Lift newLift = new Lift();
                newLift.setId(LiftManager.get(getApplicationContext()).insertLift(newLift));
                refreshList();
                Intent intent = new Intent(LiftListActivity.this, LiftPagerActivity.class);
                intent.putExtra(Lift.EXTRA_LIFT, newLift);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onLiftSelected(Lift lift) {
        Log.i(TAG, lift.toString());
        Intent intent = new Intent(this, LiftPagerActivity.class);
        intent.putExtra(Lift.EXTRA_LIFT, lift);
        startActivity(intent);
    }

    private void refreshList() {
        LiftListFragment listFragment =  (LiftListFragment) getFragmentManager().findFragmentById(R.id.lift_list_lift_fragment);
        listFragment.getLoaderManager().restartLoader(LiftListFragment.ID_LOAD_LIFTS, null, listFragment);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_lift_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
