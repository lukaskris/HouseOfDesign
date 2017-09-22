package com.leaksoft.app.houseofdesign.account;

import android.app.DatePickerDialog;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.leaksoft.app.houseofdesign.R;
import com.leaksoft.app.houseofdesign.model.Customer;
import com.leaksoft.app.houseofdesign.util.DateUtil;
import com.leaksoft.app.houseofdesign.util.PreferencesUtil;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.leaksoft.app.houseofdesign.services.ServiceFactory.service;

public class EditProfileActivity extends AppCompatActivity {

    private Customer customer;

    private Calendar mDateBirth;
    private EditText mDate;
    private EditText mName;
    private EditText mPhone;
    private Spinner mGender;
    private Button mUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        setTitle("Edit Profile");

        if(getSupportActionBar()!=null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDateBirth = new GregorianCalendar();
        mDateBirth.setTime(DateUtil.stringToDate("01 01 2000","dd MM yyyy"));
        mName = (EditText) findViewById(R.id.edit_profile_name);
        mPhone = (EditText) findViewById(R.id.edit_profile_telp);
        mGender = (Spinner) findViewById(R.id.edit_profile_jenis_kelamin);
        mDate = (EditText) findViewById(R.id.edit_profile_date);
        mUpdate = (Button) findViewById(R.id.edit_profile_update_btn);

        customer = PreferencesUtil.getUser(this);

        mName.setText(customer.getName());

        mDate.setText(DateUtil.dateToString(mDateBirth.getTime(),"dd MMMM yyyy"));
        mDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int mYear = mDateBirth.get(Calendar.YEAR);
                int mMonth = mDateBirth.get(Calendar.MONTH);
                int mDay = mDateBirth.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(EditProfileActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                        mDateBirth.set(year,monthOfYear,dayOfMonth);
                        mDate.setText(DateUtil.dateToString(mDateBirth.getTime(),"dd MMMM yyyy"));
                    }
                },mYear, mMonth,mDay);
                dialog.getDatePicker().setMaxDate(new Date().getTime());
                dialog.show();
            }
        });

        mUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isValid()){
                    final Customer newCustomer = customer;
                    newCustomer.setName(mName.getText().toString());
                    newCustomer.setPhone(mPhone.getText().toString());
                    newCustomer.setDatebirth(DateUtil.stringToDate(mDate.getText().toString(), "dd MMMM yyyy"));
                    newCustomer.setGender(mGender.getSelectedItem().toString());

                    service.updateProfile(newCustomer)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<Customer>() {
                                @Override
                                public void accept(Customer customer) throws Exception {
                                    Toast.makeText(EditProfileActivity.this, "Data profile telah diupdate", Toast.LENGTH_LONG).show();
                                    PreferencesUtil.saveUser(EditProfileActivity.this, newCustomer);
                                    finish();
                                }
                            }, new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable throwable) throws Exception {
                                    Snackbar.make(mUpdate,throwable.getLocalizedMessage(),Snackbar.LENGTH_LONG).show();
                                }
                            });
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }

        return true;
    }

    boolean isValid(){
        if(mName.getText().equals("")){
            mName.setError("Nama harus diisi");
            mName.setFocusable(true);
            return false;
        }else if(mPhone.getText().equals("")){
            mName.setError("Nomor hp harus diisi");
            mName.setFocusable(true);
            return false;
        }
        return true;
    }
}
