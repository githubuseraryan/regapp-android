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
    }
}
