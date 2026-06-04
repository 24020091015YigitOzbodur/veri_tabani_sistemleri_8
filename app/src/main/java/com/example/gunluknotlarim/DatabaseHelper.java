package com.example.gunluknotlarim;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Veritabanı ve Tablo bilgileri
    private static final String DATABASE_NAME = "NotlarDB.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "notlar_tablosu";

    // Tablo sütunları
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NOT = "not_metni";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Uygulama ilk açıldığında tabloyu oluşturur
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NOT + " TEXT)";
        db.execSQL(createTable);
    }

    // Veritabanı güncellenirse eski tabloyu silip yenisini kurar
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // 1. YENİ NOT EKLEME METODU (Create)
    public boolean notEkle(String notMetni) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NOT, notMetni);
        long sonuc = db.insert(TABLE_NAME, null, cv);
        return sonuc != -1; // Eğer -1 dönerse ekleme başarısız demektir
    }

    // 2. TÜM NOTLARI GETİRME METODU (Read)
    public Cursor tumNotlariGetir() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
    }

    // 3. NOT GÜNCELLEME METODU (Update)
    public boolean notGuncelle(String id, String yeniNot) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NOT, yeniNot);
        long sonuc = db.update(TABLE_NAME, cv, COLUMN_ID + "=?", new String[]{id});
        return sonuc > 0;
    }

    // 4. NOT SİLME METODU (Delete)
    public boolean notSil(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        long sonuc = db.delete(TABLE_NAME, COLUMN_ID + "=?", new String[]{id});
        return sonuc > 0;
    }
}


