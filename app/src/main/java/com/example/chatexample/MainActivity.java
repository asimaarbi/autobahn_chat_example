package com.example.chatexample;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import io.crossbar.autobahn.wamp.Client;
import io.crossbar.autobahn.wamp.Session;
import io.crossbar.autobahn.wamp.types.SessionDetails;
import io.crossbar.autobahn.wamp.types.Subscription;

public class MainActivity extends AppCompatActivity {

    EditText pub_txt;
    TextView sub_txt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pub_txt = findViewById(R.id.pub_txt);
        Button pub_btn = findViewById(R.id.pub_btn);
        Button sub_btn = findViewById(R.id.sub_btn);
        sub_txt = findViewById(R.id.sub_txt);
        pub_btn.setOnClickListener(view -> {
            publish();
        });
        sub_btn.setOnClickListener(view -> {
            subscribe();
        });
    }

    private void publish() {
        Session wampSession = new Session();
        wampSession.addOnJoinListener(this::onJoinPublish);

        Client client = new Client(wampSession, "ws://192.168.100.44:8080/ws", "realm1");
        client.connect().whenComplete((exitInfo, throwable) -> {
            System.out.println("Exit!");
        });
    }

    private static class Profile {
        public String first_name;
        public String last_name;
        public int age;

        public Profile(String first_name, String last_name, int age) {
            this.first_name = first_name;
            this.last_name = last_name;
            this.age = age;
        }
    }

    private void onJoinPublish(Session session, SessionDetails details) {
        System.out.println("Joined realm");
        Map<String, String> map = new HashMap<>();
        map.put("name", "omer");
        map.put("city", "multan");

        Profile profile = new Profile("omer", "akram", 21);
        session.publish("pk.codebase.profile", pub_txt.getText().toString().trim()
        ).whenComplete((publication, throwable) -> {
            System.out.println("Published ");
        });

//        CompletableFuture<Subscription> future = session.subscribe(
//                "pk.codebase.profile", this::onProfile);
//
//        future.thenAccept(subscription -> {
//
//        });
//
//        future.exceptionally(throwable -> {
//            throwable.printStackTrace();
//            return null;
//        });
    }

    public void subscribe(){
        Session wampSession = new Session();
        wampSession.addOnJoinListener(this::onJoinSubscribe);

        Client client = new Client(wampSession, "ws://192.168.100.44:8080/ws", "realm1");
        client.connect().whenComplete((exitInfo, throwable) -> {
            System.out.println("Exit!");
        });

    }

    private void onJoinSubscribe(Session session, SessionDetails details) {
        System.out.println("Joined realm");

        CompletableFuture<Subscription> future = session.subscribe(
                "pk.codebase.profile", this::onProfile);

        future.thenAccept((Subscription subscription) -> {

        });

        future.exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });
    }


    private void onProfile(List<Object> items) {
        System.out.println(items);
//        Toast.makeText(this, ""+items, Toast.LENGTH_SHORT).show();
        sub_txt.setText(""+ items);
    }
}
