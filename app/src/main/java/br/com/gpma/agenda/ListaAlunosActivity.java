package br.com.gpma.agenda;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;
import java.util.concurrent.ThreadFactory;

import br.com.gpma.agenda.adapter.AlunosAdapter;
import br.com.gpma.agenda.converter.AlunoConverter;
import br.com.gpma.agenda.dao.AlunoDAO;
import br.com.gpma.agenda.modelo.Aluno;

public class ListaAlunosActivity extends AppCompatActivity {

    public static final int CODIGO_SMS = 234;
    public static final int CODIGO_CHAMADA = 123;
    private ListView listaAlunos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_alunos);

        listaAlunos = (ListView) findViewById(R.id.lista_alunos);

        listaAlunos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Aluno aluno = (Aluno) listaAlunos.getItemAtPosition(position);
                Intent intent = new Intent(ListaAlunosActivity.this, FormularioActivity.class);
                intent.putExtra("aluno", aluno);
                startActivity(intent);
            }
        });

        registerForContextMenu(listaAlunos);

        if(ActivityCompat.checkSelfPermission(ListaAlunosActivity.this, Manifest.permission.RECEIVE_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ListaAlunosActivity.this, new String[]{Manifest.permission.RECEIVE_SMS}, CODIGO_SMS);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarLista();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_lista_alunos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_enviar:
                new EnviarAlunosTask(this).execute();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void carregarLista() {
        AlunoDAO dao = new AlunoDAO(this);
        List<Aluno> alunos = dao.Listar();
        dao.close();
        AlunosAdapter adp = new AlunosAdapter(this, alunos);
        listaAlunos.setAdapter(adp);
    }

    public void novoAluno(View v) {
        Intent intent = new Intent(ListaAlunosActivity.this, FormularioActivity.class);
        startActivity(intent);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, final ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        final Aluno aluno = (Aluno) listaAlunos.getItemAtPosition(info.position);

        String site = aluno.getSite();
        if ((site != null) && (site != "")) {
            MenuItem menuSite = menu.add("Visitar site");
            Intent intentSite = new Intent(Intent.ACTION_VIEW);
            if (!site.startsWith("http://")) {
                site = "http://" + site;
            }
            intentSite.setData(Uri.parse(site));
            menuSite.setIntent(intentSite);
        }

        MenuItem menuLigar = menu.add("Ligar");
        menuLigar.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(ActivityCompat.checkSelfPermission(ListaAlunosActivity.this, Manifest.permission.CALL_PHONE)
                    != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(ListaAlunosActivity.this,
                            new String[] { Manifest.permission.CALL_PHONE }, CODIGO_CHAMADA);
                } else {
                    String telefone = aluno.getTelefone();
                    Intent intentLigar = new Intent(Intent.ACTION_CALL);
                    intentLigar.setData(Uri.parse("tel:" + telefone));
                    startActivity(intentLigar);
                }

                return false;
            }
        });

        String sms = aluno.getTelefone();
        if((sms != null)&&(sms != "")) {
            MenuItem menuSMS = menu.add("Enviar SMS");
            Intent intentSMS = new Intent(Intent.ACTION_VIEW);
            intentSMS.setData(Uri.parse("sms:" + sms));
            menuSMS.setIntent(intentSMS);
        }

        String endereco = aluno.getEndereco();
        if((endereco != null)&&(endereco != "")) {
            MenuItem menuMapa = menu.add("Ver Mapa do Endereço");
            Intent intentMapa = new Intent(Intent.ACTION_VIEW);
            intentMapa.setData(Uri.parse("geo:0,0?q=" + endereco));
            menuMapa.setIntent(intentMapa);
        }

        MenuItem menuExluir = menu.add("Excluir");
        menuExluir.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                AlunoDAO dao = new AlunoDAO(ListaAlunosActivity.this);
                dao.Excluir(aluno);
                dao.close();
                carregarLista();

                Toast.makeText(ListaAlunosActivity.this, "Aluno " + aluno.getNome() + " excluído com sucesso.", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }
}
