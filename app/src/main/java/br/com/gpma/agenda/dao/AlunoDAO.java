package br.com.gpma.agenda.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import br.com.gpma.agenda.modelo.Aluno;

/**
 * Created by Gustavo on 28/04/2016.
 */

public class AlunoDAO extends SQLiteOpenHelper {

    public AlunoDAO(Context context) {
        super(context, "Agenda", null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE alunos (id INTEGER PRIMARY KEY, nome TEXT NOT NULL, endereco TEXT, telefone TEXT, site TEXT, nota REAL, caminhoFoto TEXT);";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "";

        switch (oldVersion) {
            case 1:
                sql = "ALTER TABLE alunos ADD COLUMN caminhoFoto TEXT";
                db.execSQL(sql);
        }
    }

    public List<Aluno> Listar() {
        Aluno aluno;
        List<Aluno> alunos = new ArrayList<Aluno>();

        String sql = "SELECT * FROM alunos ORDER BY nome";
        SQLiteDatabase db = getReadableDatabase();

        Cursor c = db.rawQuery(sql, null);
        while(c.moveToNext()) {
            aluno = new Aluno();
            aluno.setId(c.getLong(c.getColumnIndex("id")));
            aluno.setNome(c.getString(c.getColumnIndex("nome")));
            aluno.setEndereco(c.getString(c.getColumnIndex("endereco")));
            aluno.setTelefone(c.getString(c.getColumnIndex("telefone")));
            aluno.setSite(c.getString(c.getColumnIndex("site")));
            aluno.setNota(c.getDouble(c.getColumnIndex("nota")));
            aluno.setCaminhoFoto(c.getString(c.getColumnIndex("caminhoFoto")));
            alunos.add(aluno);
        }
        c.close();

        return alunos;
    }

    public Aluno Obter(Aluno aluno) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT * FROM alunos WHERE id = ?";
        Cursor c = db.rawQuery(sql, new String[] { aluno.getId().toString() });
        if(c.moveToNext()) {
            aluno = new Aluno();
            aluno.setId(c.getLong(c.getColumnIndex("id")));
            aluno.setNome(c.getString(c.getColumnIndex("nome")));
            aluno.setEndereco(c.getString(c.getColumnIndex("endereco")));
            aluno.setTelefone(c.getString(c.getColumnIndex("telefone")));
            aluno.setSite(c.getString(c.getColumnIndex("site")));
            aluno.setNota(c.getDouble(c.getColumnIndex("nota")));
            aluno.setCaminhoFoto(c.getString(c.getColumnIndex("caminhoFoto")));
        }
        c.close();
        return aluno;
    }

    public void Inserir(Aluno aluno) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues dados = getContentValues(aluno);
        db.insert("alunos", null, dados);
    }

    public void Alterar(Aluno aluno) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues dados = getContentValues(aluno);
        String[] params = { aluno.getId().toString()};
        db.update("alunos", dados, "id = ?", params);
    }

    @NonNull
    private ContentValues getContentValues(Aluno aluno) {
        ContentValues dados = new ContentValues();
        dados.put("nome", aluno.getNome());
        dados.put("endereco", aluno.getEndereco());
        dados.put("telefone", aluno.getTelefone());
        dados.put("site", aluno.getSite());
        dados.put("nota", aluno.getNota());
        dados.put("caminhoFoto", aluno.getCaminhoFoto());
        return dados;
    }

    public void Excluir(Aluno aluno) {
        SQLiteDatabase db = getWritableDatabase();
        String[] params = { aluno.getId().toString()};
        db.delete("alunos", "id = ?", params);

    }

    public boolean IsAluno(String telefone) {
        int conta = 0;
        SQLiteDatabase db = getReadableDatabase();
        String[] parms = { telefone };
        Cursor c = db.rawQuery("SELECT * FROM alunos WHERE telefone = ?", parms);
        conta = c.getCount();
        c.close();
        return (conta > 0);
    }

}
