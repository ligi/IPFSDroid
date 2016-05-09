package org.ligi.ipfsdroid;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private IPFSBinaryController ipfsBinaryInstaller;

    @BindView(R.id.installButton)
    Button installButton;

    @BindViews(R.id.version)
    List<Button> actionButtons;

    @OnClick(R.id.installButton)
    void onInstallClick() {
        ipfsBinaryInstaller.copy();
        refresh();
    }

    @OnClick(R.id.version)
    void onVersionClick() {
        new AlertDialog.Builder(this).setMessage(ipfsBinaryInstaller.run("version")).show();
    }

    @OnClick(R.id.exampleButton)
    void onButtonClick() {
        final Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("http://ligi.de/ipfs/example_links.html"));
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ipfsBinaryInstaller = new IPFSBinaryController(this);

        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    private void refresh() {
        installButton.setVisibility(ipfsBinaryInstaller.getFile().exists() ? View.GONE : View.VISIBLE);

        final int actionButtonsVisibility = ipfsBinaryInstaller.getFile().exists() ? View.VISIBLE : View.GONE;

        for (final Button actionButton : actionButtons) {
            actionButton.setVisibility(actionButtonsVisibility);
        }
    }

}
