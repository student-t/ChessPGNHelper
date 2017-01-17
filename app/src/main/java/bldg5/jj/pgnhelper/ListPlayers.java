package bldg5.jj.pgnhelper;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bldg5.jj.pgnhelper.adapters.ApiEndpoint;
import bldg5.jj.pgnhelper.adapters.ListViewAdapter;
import bldg5.jj.pgnhelper.common.Player;
import bldg5.jj.pgnhelper.adapters.RestClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ListPlayers extends AppCompatActivity {
    private static String BASE_URL = "http://ec2-52-91-157-174.compute-1.amazonaws.com:8080/find_player/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        final ListView listView = (ListView) findViewById(R.id.playersList);
        getPlayers(listView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                int selected_id = Integer.valueOf(((TextView) (view.findViewById(R.id.txtId))).getText().toString());

                Bundle params = new Bundle();
                params.putInt("PlayerId", selected_id);

                Intent navListGames = new Intent(ListPlayers.this, ListGames.class);
                navListGames.putExtras(params);
                ListPlayers.this.startActivity(navListGames);
            }
        });
    }


    public void getPlayers(final ListView lv) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiEndpoint service = retrofit.create(ApiEndpoint.class);
        Call<List<Player>> call = service.getPlayers("vish");

        call.enqueue(new Callback<List<Player>>() {

            @Override
            public void onResponse(Call<List<Player>> call, Response<List<Player>> response) {
                boolean bSuccess = response.isSuccessful();

                HashMap<String, String> hashMap = new HashMap<String, String>() {};
                if (bSuccess) {
                    List<Player> results = response.body();

                    for(final Player p: results) {
                        hashMap.put(p.getName(), String.valueOf(p.getId()));
                    }
                }

                ListViewAdapter playersListAdapter = new ListViewAdapter(hashMap);
                lv.setAdapter(playersListAdapter);
            }

            @Override
            public void onFailure(Call<List<Player>> call, Throwable t) {
                Log.e("PGNHelper", t.toString());
            }
        });
    }

}
