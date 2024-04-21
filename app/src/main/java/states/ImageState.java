package states;

import android.content.res.Configuration;
import android.util.Pair;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Gallery;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import gallery.BigImageAdapter;
import gallery.FullScreenGallery;
import gallery.ImageAdapter;
import main.*;
import resources.ResourceManager;
import threads.ImageDownloadingThread;
import utils.InfoText;
import utils.TitleTextView;
//import fraguel.android.gallery.BigImageAdapter;
//import fraguel.android.gallery.FullScreenGallery;
//import fraguel.android.gallery.ImageAdapter;
//import fraguel.android.resources.ResourceManager;
//import fraguel.android.threads.ImageDownloadingThread;


public class ImageState extends State {

    public static final int STATE_ID = 4;
    public static final int INFOSTATE_STOP_RECORD = 1;
    public static final int INFOSTATE_REPEAT_RECORD = 2;
    public static final int INFOSTATE_SPEECH = 3;
    public static final int INFOSTATE_STOP_SPEECH = 4;

    private TitleTextView title;
    private InfoText text;
    private Gallery gallery;
    private ScrollView sv;
    private FullScreenGallery bigGallery;
    private ImageAdapter imageAdapter;
    private BigImageAdapter bigAdapter;
    private int currentIndex;
    private boolean isBigGalleryDisplayed, isPresentation, automaticChange,
            stop, orientationChange;
    private int presentationIndex = 0;

    public ImageState() {
        super();
        id = STATE_ID;
    }

    @Override
    public void load() {
        System.gc();
        viewGroup = new LinearLayout(ProjectAR.getInstance()
                .getApplicationContext());
        ((LinearLayout) viewGroup).setOrientation(LinearLayout.VERTICAL);

        title = new TitleTextView(ProjectAR.getInstance().getApplicationContext());
        title.setGravity(Gravity.CENTER_HORIZONTAL);

        isBigGalleryDisplayed = false;
        isPresentation = false;
        automaticChange = false;
        stop = false;
        orientationChange = false;

        setParamsSmallGallery();

        setParamsBigGallery();

        sv = new ScrollView(ProjectAR.getInstance().getApplicationContext());
        sv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));

        text = new InfoText(ProjectAR.getInstance().getApplicationContext());

        sv.addView(text);

        loadViews();

        currentIndex = -1;

        ProjectAR.getInstance().addView(viewGroup);
        gallery.setSelection(0, true);

    }

    @Override
    public boolean loadData(Route r, PointOI p) {
        super.loadData(r, p);
        title.setText(p.title + " - " + r.name);
        String[] imgs = new String[p.images.size()];
        int i = 0;
        for (Pair<String, String> s : p.images) {
            imgs[i] = s.second;
            i++;
        }
        imageAdapter.setData(p.images);
        bigAdapter.setData(p.images);
        this.imageThread = new ImageDownloadingThread(imgs, "point"
                + Integer.toString(point.id) + "images", ResourceManager
                .getInstance().getRootPath()
                + "/tmp/"
                + "route"
                + Integer.toString(route.id) + "/");
        imageThread.start();
        imageAdapter.notifyDataSetChanged();
        bigAdapter.notifyDataSetChanged();
        return true;
    }

    @Override
    public void unload() {
        imageAdapter.recycleBitmap();
        bigAdapter.recycleBitmap();
        System.gc();
        super.unload();
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onUtteranceCompleted(String id) {

        if (!stop) {
            presentationIndex++;
            if (presentationIndex < bigGallery.getCount()) {
                automaticChange = true;
                bigGallery.setSelection(presentationIndex, true);

            } else {
                isPresentation = false;
                bigGallery.setKeepScreenOn(false);

            }

        } else
            stop = false;
    }

    private void setParamsSmallGallery() {
        gallery = new Gallery(ProjectAR.getInstance().getApplicationContext());
        imageAdapter = new ImageAdapter(ProjectAR.getInstance()
                .getApplicationContext());
        gallery.setAdapter(imageAdapter);
        gallery.setHorizontalScrollBarEnabled(true);

        gallery.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                if (currentIndex == position) {
                    viewGroup.removeAllViews();
                    viewGroup.addView(bigGallery);
                    bigGallery.setSelection(position, true);
                    currentIndex = -1;
                    isBigGalleryDisplayed = true;
                    ProjectAR.getInstance().stopTalking();
                } else
                    currentIndex = position;
            }
        });

        gallery.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int position, long arg3) {

                text.setText(point.images.get(position).first);
                if (ProjectAR.getInstance().isTalking())
                    ProjectAR.getInstance().stopTalking();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

    }

    private void setParamsBigGallery() {
        bigGallery = new FullScreenGallery(ProjectAR.getInstance()
                .getApplicationContext());
        bigAdapter = new BigImageAdapter(ProjectAR.getInstance()
                .getApplicationContext());
        bigGallery.setAdapter(bigAdapter);
        bigGallery.setHorizontalScrollBarEnabled(true);

        bigGallery.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                if (currentIndex == position) {
                    viewGroup.removeAllViews();
                    loadViews();
                    gallery.setSelection(position, true);
                    currentIndex = -1;
                    isBigGalleryDisplayed = false;
                    ProjectAR.getInstance().stopTalking();
                } else
                    currentIndex = position;

            }
        });

        bigGallery.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int position, long arg3) {

                text.setText(point.images.get(position).first);
                if (!isPresentation) {
                    if (ProjectAR.getInstance().isTalking())
                        ProjectAR.getInstance().stopTalking();
                } else {

                    if (orientationChange) {
                        // wait until gallery complete it's orientation change
                    } else if (automaticChange) {
                        ProjectAR.getInstance().talkSpeech(
                                (String) text.getText(), position);
                        automaticChange = false;
                    } else {
                        presentationIndex = position;
                        ProjectAR.getInstance().stopTalking();
                        ProjectAR.getInstance().talkSpeech(
                                (String) text.getText(), position);
                        stop = true;
                    }

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

    }

    public void changeOrientationFinished() {
        orientationChange = false;
    }

    private void loadViews() {
        viewGroup.addView(title);
        viewGroup.addView(gallery);
        viewGroup.addView(sv);

    }

    @Override
    public boolean onConfigurationChanged(Configuration newConfig) {

        if (isBigGalleryDisplayed) {
            switch (newConfig.orientation) {
                case Configuration.ORIENTATION_LANDSCAPE:
                    bigGallery.setOrientationChanged(true);
                    orientationChange = true;
                    break;

                case Configuration.ORIENTATION_PORTRAIT:
                    bigGallery.setOrientationChanged(true);
                    orientationChange = true;
                    break;

                case Configuration.ORIENTATION_SQUARE:
                    break;
                case Configuration.ORIENTATION_UNDEFINED:
                    break;
            }

        }

        return true;
    }

    @Override
    public Menu onCreateStateOptionsMenu(Menu menu) {
        menu.clear();
        if (!isPresentation)
            menu.add(0, INFOSTATE_SPEECH, 0,
                    R.string.infostate_menu_speechPresentation).setIcon(
                    R.drawable.ic_menu_talkplay);
        else
            menu.add(0, INFOSTATE_STOP_SPEECH, 0,
                    R.string.infostate_menu_stopPresentation).setIcon(
                    R.drawable.ic_menu_talkstop);
        menu.add(0, INFOSTATE_STOP_RECORD, 0, R.string.infostate_menu_stop)
                .setIcon(R.drawable.ic_menu_talkstop);
        menu.add(0, INFOSTATE_REPEAT_RECORD, 0, R.string.infostate_menu_repeat)
                .setIcon(R.drawable.ic_menu_talkplay);

        return menu;
    }

    @Override
    public boolean onStateOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case INFOSTATE_STOP_RECORD:
                ProjectAR.getInstance().stopTalking();
                return true;

            case INFOSTATE_REPEAT_RECORD:
                ProjectAR.getInstance().talk((String) text.getText());
                return true;
            case INFOSTATE_SPEECH:
                if (!isBigGalleryDisplayed) {
                    viewGroup.removeAllViews();
                    viewGroup.addView(bigGallery);
                    currentIndex = -1;
                    isBigGalleryDisplayed = true;
                }
                bigGallery.setSelection(0, true);
                presentationIndex = 0;
                ProjectAR.getInstance().talkSpeech((String) text.getText(), 0);
                bigGallery.setKeepScreenOn(true);
                isPresentation = true;
                return true;
            case INFOSTATE_STOP_SPEECH:
                ProjectAR.getInstance().stopTalking();
                isPresentation = false;
                viewGroup.removeAllViews();
                loadViews();
                gallery.setSelection(0, true);
                currentIndex = -1;
                isBigGalleryDisplayed = false;
                return true;

        }
        return false;
    }

    @Override
    public void imageLoaded(int index) {
        if (!isBigGalleryDisplayed)
            imageAdapter.notifyDataSetChanged();
        else
            bigAdapter.notifyDataSetChanged();

    }

}
