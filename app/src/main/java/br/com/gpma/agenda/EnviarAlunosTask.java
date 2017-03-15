package br.com.gpma.agenda;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.util.List;

import br.com.gpma.agenda.converter.AlunoConverter;
import br.com.gpma.agenda.dao.AlunoDAO;
import br.com.gpma.agenda.modelo.Aluno;

/**
 * Created by Gustavo on 05/05/2016.
 */
public class EnviarAlunosTask extends AsyncTask<Object, Object, String> {
    private Context context;
    private ProgressDialog dialog;

    public EnviarAlunosTask(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(Object[] params) {
        AlunoDAO dao = new AlunoDAO(context);
        List<Aluno> alunos = dao.Listar();
        dao.close();

        AlunoConverter converter = new AlunoConverter();
        String json = converter.ConverteParaJson(alunos);

        WebClient client = new WebClient();
        String resposta = client.post(json);

        return resposta;
    }

    @Override
    protected void onPostExecute(String resposta) {
        dialog.dismiss();
        Toast.makeText(context, resposta, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onPreExecute() {
        dialog = ProgressDialog.show(context, "Aguarde", "Enviando alunos...", true, true);
    }
}
