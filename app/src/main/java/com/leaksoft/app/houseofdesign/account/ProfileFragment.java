package com.leaksoft.app.houseofdesign.account;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.leaksoft.app.houseofdesign.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.Manifest.permission.CAMERA;
import static android.app.Activity.RESULT_OK;
import static android.support.v4.content.PermissionChecker.checkSelfPermission;

public class ProfileFragment extends Fragment {

    private RelativeLayout mProfile;

    private TextView mName;
    private TextView mEmail;
    private CircleImageView mPicture;
    private CircleImageView mUpload;
    private static final int CAMERA_PICK = 1;
    private static final int PICK_FROM_GALLERY = 2;
    private final static int ALL_PERMISSIONS_RESULT = 107;
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected;
    private ArrayList<String> permissions;
    Uri picUri;
    Bitmap myBitmap;

    public ProfileFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        setHasOptionsMenu(true);

        getActivity().setTitle("Profile");

        mName = (TextView) view.findViewById(R.id.profile_nama);
        mEmail = (TextView) view.findViewById(R.id.profile_email);
        mPicture = (CircleImageView) view.findViewById(R.id.profile_image);
        mProfile = (RelativeLayout) view.findViewById(R.id.profile_alamat);
        mUpload = (CircleImageView) view.findViewById(R.id.profile_camera_gallery);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            mName.setText(user.getDisplayName());
            mEmail.setText(user.getEmail());
            Uri uri = user.getPhotoUrl();
            Glide.with(getContext()).load(uri).diskCacheStrategy(DiskCacheStrategy.RESULT).override(400, 400).into(mPicture);
        }

        mUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(getPickImageChooserIntent(), 200);
            }
        });
        permissions = new ArrayList<>();
        permissionsRejected = new ArrayList<>();


        permissions.add(CAMERA);
        permissionsToRequest = findUnAskedPermissions(permissions);

        requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, ALL_PERMISSIONS_RESULT);

        mProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), AddressActivity.class));
                getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            }
        });
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.profile_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap bitmap;
        if (requestCode == 200 && resultCode == RESULT_OK) {
            if (getPickImageResultUri(data) != null) {
                picUri = getPickImageResultUri(data);
//                try {
//                    myBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), picUri);
//                    myBitmap = getResizedBitmap(myBitmap, 500);
                    CropImage.activity(picUri).setGuidelines(CropImageView.Guidelines.ON)
                            .setCropShape(CropImageView.CropShape.OVAL)
                            .setFixAspectRatio(true)
                            .start(getContext(),this);
//                    myBitmap = rotateImageIfRequired(myBitmap, picUri);
//                    mPicture.setImageBitmap(myBitmap);
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
            } else {
                bitmap = (Bitmap) data.getExtras().get("data");
                myBitmap = bitmap;
                mPicture.setImageBitmap(myBitmap);
            }

        }else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                Glide.with(getContext()).load(resultUri).override(400,400).into(mPicture);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(getContext(),error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case ALL_PERMISSIONS_RESULT:
                for (Object perms : permissionsToRequest) {
                    if (!hasPermission(perms.toString())) {
                        permissionsRejected.add(perms.toString());
                    }
                }

                if (permissionsRejected.size() > 0) {


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    });
                            return;
                        }
                    }

                }

                break;
        }


    }

    public Intent getPickImageChooserIntent() {

        // Determine Uri of camera image to save.
        Uri outputFileUri = getCaptureImageOutputUri();

        List<Intent> allIntents = new ArrayList<>();
        PackageManager packageManager = getContext().getPackageManager();

        // collect all camera intents
        Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            }
            allIntents.add(intent);
        }

        // collect all gallery intents
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        List<ResolveInfo> listGallery = packageManager.queryIntentActivities(galleryIntent, 0);
        for (ResolveInfo res : listGallery) {
            Intent intent = new Intent(galleryIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            allIntents.add(intent);
        }

        // the main intent is the last in the list (fucking android) so pickup the useless one
        Intent mainIntent = allIntents.get(allIntents.size() - 2);
        for (Intent intent : allIntents) {
            if (intent.getComponent().getClassName().equals("com.android.documentsui.DocumentsActivity")) {
                mainIntent = intent;
                break;
            }
        }
        allIntents.remove(mainIntent);

        // Create a chooser from the main intent
        Intent chooserIntent = Intent.createChooser(mainIntent, "Select source");

        // Add all other intents
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));

        return chooserIntent;
    }

    private Uri getCaptureImageOutputUri() {
        Uri outputFileUri = null;
        File getImage = getContext().getExternalCacheDir();
        if (getImage != null) {
            outputFileUri = Uri.fromFile(new File(getImage.getPath(), "profile.png"));
        }
        return outputFileUri;
    }
//
//    private Bitmap rotateImageIfRequired(Bitmap img, Uri selectedImage) throws IOException {
//
//        ExifInterface ei = new ExifInterface(selectedImage.getPath());
//        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
//        int orientation2 = getRotation(getContext(),selectedImage);
//        Log.d("Orientation 1", String.valueOf(orientation));
//        Log.d("Orientation 2", String.valueOf(orientation2));
////        return rotateImage(img,orientation2);
//        switch (orientation) {
//            case ExifInterface.ORIENTATION_ROTATE_90:
//                return rotateImage(img, 90);
//            case ExifInterface.ORIENTATION_ROTATE_180:
//                return rotateImage(img, 180);
//            case ExifInterface.ORIENTATION_ROTATE_270:
//                return rotateImage(img, 270);
//            default:
//                return img;
//        }
//    }
//
//    private static Bitmap rotateImage(Bitmap img, int degree) {
//        Matrix matrix = new Matrix();
//        matrix.postRotate(degree);
//        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
////        img.recycle();
//        return rotatedImg;
//    }
//
//    private static int getRotation(Context context,Uri selectedImage) {
//
//        int rotation = 0;
//        ContentResolver content = context.getContentResolver();
//
//        Cursor mediaCursor = content.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                new String[] { "orientation", "date_added" },
//                null, null, "date_added desc");
//
//        if (mediaCursor != null && mediaCursor.getCount() != 0) {
//            while(mediaCursor.moveToNext()){
//                rotation = mediaCursor.getInt(0);
//                break;
//            }
//        }
//        mediaCursor.close();
//        return rotation;
//    }
//
//    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
//        int width = image.getWidth();
//        int height = image.getHeight();
//
//        float bitmapRatio = (float) width / (float) height;
//        if (bitmapRatio > 0) {
//            width = maxSize;
//            height = (int) (width / bitmapRatio);
//        } else {
//            height = maxSize;
//            width = (int) (height * bitmapRatio);
//        }
//        return Bitmap.createScaledBitmap(image, width, height, true);
//    }

    public Uri getPickImageResultUri(Intent data) {
        boolean isCamera = true;
        if (data != null) {
            String action = data.getAction();
            isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
        }


        return isCamera ? getCaptureImageOutputUri() : data.getData();
    }
//
    private ArrayList<String> findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList<String> result = new ArrayList<>();

        for (String  perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(getContext(),permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(getContext())
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }
}
