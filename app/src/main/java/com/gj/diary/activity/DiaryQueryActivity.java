package com.gj.diary.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.gj.diary.R;
import com.gj.diary.holder.DiaryTreeHolder;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2017/6/26.
 */

public class DiaryQueryActivity extends AppCompatActivity {

    private  AndroidTreeView tView;
    private static Pattern p = Pattern.compile("\\d{4}-\\d{2}-\\d{2}\\.jpg$") ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_diary_query);
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("日记浏览");
        actionBar.show();

        TreeNode root = TreeNode.root();
        loadingTree(root);
        tView = new AndroidTreeView(this, root);
        tView.setDefaultNodeClickListener(new TreeNode.TreeNodeClickListener(){
            @Override
            public void onClick(TreeNode node, Object value) {
                DiaryTreeHolder.DiaryTreeItem diaryTreeItem = (DiaryTreeHolder.DiaryTreeItem) value;
                if(node.isLeaf()){
                    Toast.makeText(DiaryQueryActivity.this, "照片"+diaryTreeItem.getFileName()+"被点击", Toast.LENGTH_SHORT).show();
                }else{
                    ((DiaryTreeHolder)node.getViewHolder()).changeFolderIcon(node,diaryTreeItem,false);
                }
            }
        });
        tView.setDefaultContainerStyle(R.style.TreeNodeStyleCustom);
        tView.setDefaultViewHolder(DiaryTreeHolder.class);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        //设置顶部,左边布局
        params.gravity= Gravity.TOP|Gravity.LEFT;
        expandNode(root);
        addContentView(tView.getView(),params);
    }

    private void expandNode(TreeNode node) {
        node.setExpanded(true);
        List<TreeNode> children = node.getChildren();
        if(node != null && node.size() >0){
            expandNode(children.get(0));
        }
    }

    private void loadingTree( TreeNode root ) {
        File rootFile = new File( MainActivity.rootDir, MainActivity.FILE_PATH);
        File[] roots = orderByName(rootFile.listFiles());
        TreeNode node = null;
        for( int i = roots.length - 1; i >= 0; i-- ) {
            node = new TreeNode(new DiaryTreeHolder.DiaryTreeItem(roots[i].getName(),roots[i].getPath()));
            root.addChild(node);
            loadingTree( roots[i], node );
        }
    }
    /**
     * 生成树
     *
     * @param root
     * @param node void
     * @author jin.guo 2016年5月29日
     */
    private void loadingTree( File root, TreeNode node ) {
        File[] files = orderByName(root.listFiles()) ;
        TreeNode subNode = null;
        if( files == null ) {
            return;
        }
        for( int i = files.length - 1; i >= 0; i-- ) {
            String fileName = files[i].getName();
            String filePath = files[i].getPath();
            if(files[i].isFile() && fileName.endsWith("temp")){
                continue;
            }
            if(files[i].isFile()){
                if(!p.matcher(fileName).matches()){
                    continue;
                }
            }
            subNode = new TreeNode( new DiaryTreeHolder.DiaryTreeItem(fileName,filePath));
            node.addChild(subNode);
            if( files[i].isDirectory() ) {
                loadingTree( files[i], subNode );
            }
        }

    }

    public static File[] orderByName(File[] files) {
        List<File> fileList = Arrays.asList(files);
        Collections.sort(fileList, new Comparator< File>() {
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
}
