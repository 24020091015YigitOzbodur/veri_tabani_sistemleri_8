package com.example.gunluknotlarim;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    DatabaseHelper db;
    EditText editNotInput;
    Button btnEkle, btnGuncelle, btnSil;
    ListView listViewNotlar;

    ArrayList<String> notListesi;
    ArrayList<String> idListesi; // Arka planda hangi notun güncelleneceğini/silineceğini bulmak için
    ArrayAdapter<String> adapter;

    String secilenNotId = ""; // Güncelleme ve silme için tıklanan notun ID'sini tutacak

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Arayüz elemanlarını (butonları, listeyi vs.) kodla bağlama
        editNotInput = findViewById(R.id.editNotInput);
        btnEkle = findViewById(R.id.btnEkle);
        btnGuncelle = findViewById(R.id.btnGuncelle);
        btnSil = findViewById(R.id.btnSil);
        listViewNotlar = findViewById(R.id.listViewNotlar);

        db = new DatabaseHelper(this);
        notListesi = new ArrayList<>();
        idListesi = new ArrayList<>();

        notlariListele(); // Uygulama açılır açılmaz veritabanındaki notları ekrana getir

        // 1. EKLE BUTONU TIKLANINCA
        btnEkle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String notMetni = editNotInput.getText().toString();
                if (!notMetni.isEmpty()) {
                    boolean sonuc = db.notEkle(notMetni); // Veritabanına kaydet
                    if (sonuc) {
                        Toast.makeText(MainActivity.this, "Not eklendi! 🎉", Toast.LENGTH_SHORT).show();
                        editNotInput.setText(""); // Kutuyu temizle
                        notlariListele(); // Listeyi yenile ki yeni not ekranda görünsün
                    } else {
                        Toast.makeText(MainActivity.this, "Ekleme başarısız oldu!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Lütfen önce bir not yazın!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 2. LİSTEDEN BİR NOTA TIKLANINCA (Güncelleme ve Silme işlemi için notu seçme)
        listViewNotlar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Tıklanan notun ID'sini ve metnini arka planda hafızaya alıyoruz
                secilenNotId = idListesi.get(position);
                String secilenNotMetni = notListesi.get(position);

                // Seçilen notu düzenleyebilmek için tekrar yazı kutusuna atıyoruz
                editNotInput.setText(secilenNotMetni);
                Toast.makeText(MainActivity.this, "Not seçildi. Güncelleyebilir veya Silebilirsiniz.", Toast.LENGTH_SHORT).show();
            }
        });

        // 3. GÜNCELLE BUTONU TIKLANINCA
        btnGuncelle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String yeniNot = editNotInput.getText().toString();
                if (!secilenNotId.isEmpty() && !yeniNot.isEmpty()) {
                    boolean sonuc = db.notGuncelle(secilenNotId, yeniNot);
                    if (sonuc) {
                        Toast.makeText(MainActivity.this, "Not güncellendi! ✏️", Toast.LENGTH_SHORT).show();
                        editNotInput.setText(""); // Kutuyu sıfırla
                        secilenNotId = ""; // Seçimi sıfırla
                        notlariListele(); // Listeyi yenile
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Önce aşağıdaki listeden güncellenecek notu seçin!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 4. SİL BUTONU TIKLANINCA
        btnSil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!secilenNotId.isEmpty()) {
                    boolean sonuc = db.notSil(secilenNotId);
                    if (sonuc) {
                        Toast.makeText(MainActivity.this, "Not silindi! 🗑️", Toast.LENGTH_SHORT).show();
                        editNotInput.setText("");
                        secilenNotId = "";
                        notlariListele();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Önce listeden silinecek notu seçin!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // VERİTABANINDAN NOTLARI ÇEKİP LİSTEYE BASAN YARDIMCI METOT
    private void notlariListele() {
        notListesi.clear();
        idListesi.clear();

        Cursor cursor = db.tumNotlariGetir();
        if (cursor.getCount() == 0) {
            // Veritabanı boşsa uyarı ver
            Toast.makeText(this, "Henüz hiç not eklenmemiş.", Toast.LENGTH_SHORT).show();
        } else {
            while (cursor.moveToNext()) {
                idListesi.add(cursor.getString(0)); // 0. sütun ID'dir
                notListesi.add(cursor.getString(1)); // 1. sütun Not metnidir
            }
        }

        // Android'in hazır basit liste tasarımını kullanarak bizim notları ekrana basıyoruz
        adapter = new ArrayAdapter<>(this, R.layout.liste_elemani, notListesi);
        listViewNotlar.setAdapter(adapter);
    }
}