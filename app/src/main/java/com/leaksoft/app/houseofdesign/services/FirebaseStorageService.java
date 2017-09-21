package com.leaksoft.app.houseofdesign.services;

import android.content.Context;
import android.graphics.Bitmap;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class FirebaseStorageService {

    private static FirebaseStorageService instance;

    private Context mContext;
    private FirebaseStorage mStorage;
    private StorageReference mReference;

    public FirebaseStorageService(Context context){
        mContext = context;
        mStorage = FirebaseStorage.getInstance("gs://houseofdesign/");
        mReference = mStorage.getReference();
    }

    public static FirebaseStorageService getInstance(Context mContext){
        if(instance == null) instance = new FirebaseStorageService(mContext);
        return instance;
    }

    public static void clearInstance(){
        instance = null;
    }

    public UploadTask uploadPicture(Bitmap bmp, String name){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
        byte[] data = outputStream.toByteArray();
        StorageReference invoiceRef = mReference.child("invoice/"+name+".jpg");
        return invoiceRef.putBytes(data);
    }

//    public void uploadPicture(Bitmap bmp, String name) throws IOException {
//        output = new ByteArrayOutputStream();
//        bmp.compress(Bitmap.CompressFormat.JPEG,100,output);
//        byte[] photo = output.toByteArray();
//        blobId = BlobId.of(bucketName,name);
//        if(mStorage.get(blobId) == null) {
//            blobInfo = BlobInfo.newBuilder(blobId).setContentType("text/plain").build();
//            blob = mStorage.create(blobInfo, photo);
//        }else {
//            WritableByteChannel channel = blob.writer();
//            channel.write(ByteBuffer.wrap(photo));
//            channel.close();
//        }
//    }
}
