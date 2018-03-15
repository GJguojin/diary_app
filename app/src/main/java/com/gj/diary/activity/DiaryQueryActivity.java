package com.gj.diary.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.gj.diary.MyApp;
import com.gj.diary.R;
import com.gj.diary.holder.DiaryTreeHolder;
import com.gj.diary.utils.DialogUtils;
import com.gj.diary.utils.PropertiesUtil;
import com.gj.diary.view.DiaryContentDialog;
import com.gj.diary.view.DiaryLoginDialog;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;


import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2017/6/26.
 */

public class DiaryQueryActivity extends DiaryBaseActivity  {

    protected Context mContext;

    public final static int PASSWORD_CHECKED_OK = 0;

    public final static int PASSWORD_PHOTO_OK = 1;

    private AndroidTreeView tView;

    private static Pattern p = Pattern.compile("\\d{4}-\\d{2}-\\d{2}\\.jpg$");

    private static DiaryTreeHolder.DiaryTreeItem nowDiaryTreeItem;

    //定义Handler对象
    public Handler handler = new Handler() {
        @Override
        //当有消息发送出来的时候就执行Handler的这个方法
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case PASSWORD_CHECKED_OK:
                    new DiaryContentDialog(DiaryQueryActivity.this,nowDiaryTreeItem);
                    break;
                case PASSWORD_PHOTO_OK:
                    Intent intent = new Intent(mContext, DiaryImageZoomActivity.class);
                    intent.putExtra("image", nowDiaryTreeItem.getFilePath());
                    intent.putExtra("type","url");
                    intent.putExtra("name", "图片恢复");
                    startActivity(intent);
                    break;
            }
        }
    };

    @Override
    protected boolean customEnterAnimation() {
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        return true;
    }

    @Override
    protected boolean customExitAnimation() {
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        return true;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_diary_query);
        super.onCreate(savedInstanceState);
        mContext =MyApp.getAppContext();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        //actionBar.setIcon(R.mipmap.diary);
        //actionBar.setDisplayShowHomeEnabled(true);

        final String queryTitle = PropertiesUtil.getProperties(this, "query_title");
        if(queryTitle != null && !"".equals(queryTitle)){
            actionBar.setTitle(queryTitle);
        }else{
            actionBar.setTitle(PropertiesUtil.PROPERTIES.get("query_title"));
        }
        actionBar.show();

        TreeNode root = TreeNode.root();
        loadingTree(root);
        tView = new AndroidTreeView(this, root);
        tView.setDefaultNodeClickListener(new DiaryTreeNodeClickListener());
        tView.setDefaultNodeLongClickListener(new DiaryTreeNodeLongClickListener(this));
        tView.setDefaultContainerStyle(R.style.TreeNodeStyleCustom);
        tView.setDefaultViewHolder(DiaryTreeHolder.class);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        //设置顶部,左边布局
        params.gravity = Gravity.TOP | Gravity.LEFT;
        expandNode(root);
        addContentView(tView.getView(), params);
    }

    private void expandNode(TreeNode node) {
        node.setExpanded(true);
        List<TreeNode> children = node.getChildren();
        if (node != null && node.size() > 0) {
            expandNode(children.get(0));
        }
    }

    private void loadingTree(TreeNode root) {
        File rootFile = new File(MainActivity.rootDir, MainActivity.FILE_PATH);
        File[] roots = orderByName(rootFile.listFiles());
        TreeNode node = null;
        for (int i = roots.length - 1; i >= 0; i--) {
            if(roots[i].isFile() && roots[i].getName().endsWith("temp") ){
                continue;
            }
            node = new TreeNode(new DiaryTreeHolder.DiaryTreeItem(roots[i].getName(), roots[i].getPath()));
            root.addChild(node);
            loadingTree(roots[i], node);
        }
    }

    /**
     * 生成树
     *
     * @param root
     * @param node void
     * @author jin.guo 2016年5月29日
     */
    private void loadingTree(File root, TreeNode node) {
        if(root.isFile() && root.getName().endsWith("temp") ){
            return;
        }
        File[] files = orderByName(root.listFiles());
        TreeNode subNode = null;
        if (files == null) {
            return;
        }
        for (int i = files.length - 1; i >= 0; i--) {
            String fileName = files[i].getName();
            String filePath = files[i].getPath();
            if (files[i].isFile() && fileName.endsWith("temp")) {
                continue;
            }
            if (files[i].isFile()) {
                if (!p.matcher(fileName).matches()) {
                    continue;
                }
            }
            subNode = new TreeNode(new DiaryTreeHolder.DiaryTreeItem(fileName, filePath));
            node.addChild(subNode);
            if (files[i].isDirectory()) {
                loadingTree(files[i], subNode);
            }
        }

    }

    public static File[] orderByName(File[] files) {
        if(files == null){
            return new File[]{};
        }
        List<File> fileList = Arrays.asList(files);
        Collections.sort(fileList, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                if (o1.isDirectory() && o2.isFile())
                    return -1;
                if (o1.isFile() && o2.isDirectory())
                    return 1;
                return o1.getName().compareTo(o2.getName());
            }
        });
        return (File[]) fileList.toArray();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item != null) {
            switch (item.getItemId()) {
                case android.R.id.home:
                    finish();
                    break;
            }
        }
        return true;
    }

    /**
     * 树点击事件
     */
    public class DiaryTreeNodeClickListener implements TreeNode.TreeNodeClickListener {
        @Override
        public void onClick(TreeNode node, Object value) {
            DiaryTreeHolder.DiaryTreeItem diaryTreeItem = (DiaryTreeHolder.DiaryTreeItem) value;
            if (node.isLeaf()) {
                String filePath = diaryTreeItem.getFilePath();
                String fileName = diaryTreeItem.getFileName();
                File childFile = new File(filePath);
                File parentFile = childFile.getParentFile();
                File[] files = parentFile.listFiles();
                ArrayList<String> imageUrls = new ArrayList<String>();
                int index = 0;
                orderByName(files);
                for(int i=0;i<files.length;i++){
                    File file = files[i];
                    String name = file.getName();
                    if(fileName != null && fileName.equals(name)){
                        index = i;
                    }
                    imageUrls.add(file.getPath());
                }
                Intent intent = new Intent(mContext, DiaryGalleryActivity.class);
                intent.putExtra("imageUrl", imageUrls);
                intent.putExtra("name", "日记浏览");
                intent.putExtra("index",index);

                startActivity(intent);
            } else {
                ((DiaryTreeHolder) node.getViewHolder()).changeFolderIcon(node, diaryTreeItem, false);
            }
        }
    }

    public class DiaryTreeNodeLongClickListener implements TreeNode.TreeNodeLongClickListener {
        private Context context;

        public DiaryTreeNodeLongClickListener(Context context) {
            this.context = context;
        }

        @Override
        public boolean onLongClick(TreeNode node, Object value) {
            DiaryTreeHolder.DiaryTreeItem diaryTreeItem = (DiaryTreeHolder.DiaryTreeItem) value;
            if (node.isLeaf()) {
                nowDiaryTreeItem = diaryTreeItem;
                if (PropertiesUtil.diaryPassword == null) {
                    DiaryLoginDialog diaryLoginDialog = new DiaryLoginDialog(context,"1");
                    diaryLoginDialog.show();
                }else{
                    handler.sendEmptyMessage(PASSWORD_CHECKED_OK);
                }
            }
            return true;
        }
    }

}
