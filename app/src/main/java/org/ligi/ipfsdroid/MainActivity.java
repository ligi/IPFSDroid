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
import org.ligi.tracedroid.sending.TraceDroidEmailSender;

public class MainActivity extends AppCompatActivity {

    private IPFSBinaryController ipfsBinaryInstaller;

    @BindView(R.id.installButton)
    Button installButton;

    @BindViews({R.id.versionButton, R.id.gcButton, R.id.ipfsInitButton, R.id.daemonButton, R.id.swarmPeersButton , R.id.catReadmeButton})
    List<Button> actionButtons;

    @OnClick(R.id.installButton)
    void onInstallClick() {
        ipfsBinaryInstaller.copy();
        refresh();
    }

    @OnClick(R.id.daemonButton)
    void onDaemonClick() {
        startService(new Intent(this, IPFSDaemonService.class));
    }

    @OnClick(R.id.versionButton)
    void onVersionClick() {
        new AlertDialog.Builder(this).setMessage(ipfsBinaryInstaller.run("version")).show();
    }

    @OnClick(R.id.ipfsInitButton)
    void onInitClick() {
        new AlertDialog.Builder(this).setMessage(ipfsBinaryInstaller.run("init")).show();
    }

    @OnClick(R.id.gcButton)
    void onGCClick() {
        new AlertDialog.Builder(this).setMessage(ipfsBinaryInstaller.run("repo gc")).show();
    }

    @OnClick(R.id.swarmPeersButton)
    void onSwarmPeers() {
        new AlertDialog.Builder(this).setMessage(ipfsBinaryInstaller.run("swarm peers")).show();
    }

    @OnClick(R.id.catReadmeButton)
    void catReadmeButton() {
        new AlertDialog.Builder(this).setMessage(ipfsBinaryInstaller.run("cat /ipfs/QmVtU7ths96fMgZ8YSZAbKghyieq7AjxNdcqyVzxTt3qVe/readme")).show();
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

        TraceDroidEmailSender.sendStackTraces("ligi@ligi.de", this);
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
