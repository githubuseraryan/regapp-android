package appcom.example.regsplashscreen;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import appcom.example.regsplashscreen.model.User;
import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;

public class AdminScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_screen);

        TableView<User> table = (TableView<User>) findViewById(R.id.as_table_view);
        table.setHeaderAdapter(new SimpleTableHeaderAdapter(this, "User name", "User active", "Enable", "Disable"));

       /* LayoutInflater inflater = getLayoutInflater();
        TableLayout tableLayout = findViewById(R.id.main_table);

        TableRow.LayoutParams rowParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 0, 1f *//*0.33f*//*);

        TableRow tableRow1 = (TableRow)inflater.inflate(R.layout.item_stream_row, tableLayout, false);
        tableRow1.setLayoutParams(rowParams);

        TableRow tableRow2 = (TableRow)inflater.inflate(R.layout.item_stream_row, tableLayout, false);
        tableRow2.setLayoutParams(rowParams);

        TableRow tableRow3 = (TableRow)inflater.inflate(R.layout.item_stream_row, tableLayout, false);
        tableRow3.setLayoutParams(rowParams);

        tableLayout.setWeightSum(1f);

        tableLayout.addView(tableRow1);
        tableLayout.addView(tableRow2);
        tableLayout.addView(tableRow3);*/

    }
}
