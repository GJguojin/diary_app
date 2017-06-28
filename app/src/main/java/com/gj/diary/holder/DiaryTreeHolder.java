package com.gj.diary.holder;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gj.diary.activity.MainActivity;
import com.gj.diary.view.DiaryImageView;
import com.unnamed.b.atv.model.TreeNode;

import com.gj.diary.R;
/**
 * Created by Administrator on 2017/6/26.
 */

public class DiaryTreeHolder extends TreeNode.BaseNodeViewHolder<DiaryTreeHolder.DiaryTreeItem> {


    public DiaryTreeHolder(Context context) {
        super(context);
    }

    private  ImageView folder;
    private  ImageView arrow;
    private static Bitmap openFolder;
    private static  Bitmap closeFolder;
    private static Bitmap arrowDown;
    private static  Bitmap arrowRight;
    private   View forderView;
    private   View pictureView;
    private  TextView diaryFolderValue;

    @Override
    public View createNodeView(TreeNode node, DiaryTreeItem value) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        if(node.isLeaf()){
            if(value.fileImg  == null){
                BitmapFactory.Options newOpts = new BitmapFactory.Options();
                newOpts.inSampleSize = 4;
                Bitmap bitmap = BitmapFactory.decodeFile(value.filePath,newOpts);
                bitmap = MainActivity.getRoundedCornerBitmap(bitmap, 15);
                value.setFileImg(bitmap);
            }
            pictureView = inflater.inflate(R.layout.diary_query_pictue, null, false);
            DiaryImageView picture = (DiaryImageView)pictureView.findViewById(R.id.diary_query_picture);
            picture.setImageBitmap(value.fileImg);

            final TextView pictureText = (TextView) pictureView.findViewById(R.id.diary_node_value);
            String fileName = value.getFileName();
            if(fileName.endsWith("jpg")){
                fileName = fileName.substring(0,10);
            }
            pictureText.setText(fileName);

            return pictureView;
        }else{
            if(forderView == null){
                forderView = inflater.inflate(R.layout.diary_query_folder, null, false);
            }
            changeFolderIcon(node,value,true);
            return forderView;
        }
    }

    public void changeFolderIcon(TreeNode node, DiaryTreeItem value,boolean flag){
        if(forderView == null){
            return;
        }
        diaryFolderValue = (TextView)forderView.findViewById(R.id.diary_node_value);
        folder = (ImageView)forderView.findViewById(R.id.diary_folder);
        arrow = (ImageView) forderView.findViewById(R.id.diary_arrow);
        if(openFolder == null){
            openFolder = BitmapFactory.decodeResource(context.getResources(), R.drawable.folder_open);
        }
        if(closeFolder == null){
            closeFolder = BitmapFactory.decodeResource(context.getResources(), R.drawable.folder_close);
        }
        if(arrowDown == null){
            arrowDown = BitmapFactory.decodeResource(context.getResources(), R.drawable.arrow_down);
        }
        if(arrowRight == null){
            arrowRight = BitmapFactory.decodeResource(context.getResources(), R.drawable.arrow_right);
        }
        if(flag){
            if(node.isExpanded()){
                folder.setImageBitmap(openFolder);
                arrow.setImageBitmap(arrowDown);
            }else{
                folder.setImageBitmap(closeFolder);
                arrow.setImageBitmap(arrowRight);

            }
        }else{
            if(node.isExpanded()){
                folder.setImageBitmap(closeFolder);
                arrow.setImageBitmap(arrowRight);
            }else{
                folder.setImageBitmap(openFolder);
                arrow.setImageBitmap(arrowDown);
            }
        }

        diaryFolderValue.setText(value.fileName);
    }


    public static class DiaryTreeItem {
        private String fileName;
        private String filePath;
        private Bitmap fileImg;

        public DiaryTreeItem(String fileName, String filePath) {
            this.fileName = fileName;
            this.filePath = filePath;
        }
        public void setFileImg(Bitmap fileImg) {
            this.fileImg = fileImg;
        }

        public String getFileName() {
            return fileName;
        }

        public String getFilePath() {
            return filePath;
        }

        public Bitmap getFileImg() {
            return fileImg;
        }
    }
}
