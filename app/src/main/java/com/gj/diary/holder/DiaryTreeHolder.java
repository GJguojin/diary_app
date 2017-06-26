package com.gj.diary.holder;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.unnamed.b.atv.model.TreeNode;

/**
 * Created by Administrator on 2017/6/26.
 */

public class DiaryTreeHolder extends TreeNode.BaseNodeViewHolder<DiaryTreeHolder.DiaryTreeItem> {

    public DiaryTreeHolder(Context context) {
        super(context);
    }

    @Override
    public View createNodeView(TreeNode node, DiaryTreeItem value) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        if(node.isLeaf()){
            if(value.fileImg  == null){
                BitmapFactory.Options newOpts = new BitmapFactory.Options();
                newOpts.inSampleSize = 4;
                Bitmap bitmap = BitmapFactory.decodeFile(value.filePath,newOpts);
                value.setFileImg(bitmap);
            }
            ImageView imageView = new ImageView(context);
            imageView.setImageBitmap(value.fileImg);
            return imageView;
        }else{
            TextView view = new TextView(context);
            view.setText(value.fileName);
            return view;
        }
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
