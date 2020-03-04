package com.example.tipcalculator_counter;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import android.content.DialogInterface;
import android.os.Bundle;


import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;



public class MainActivity extends AppCompatActivity {

    EditText amount;
    EditText amtOfPeople;
    EditText customTipAmt;
    TextView tipTotalPerPerson;
    TextView totalFinal;

    public TextView tipAmountFinal;
    private RadioGroup tipAmounts;
    private RadioButton fifthteenP;
    private RadioButton eighteenP;
    private RadioButton twentyP;
    private RadioButton customAmt;
    public Button submit;
    public double customTipAmount;
    private Button clear;
    private int radioCheckedId = -1;

    final static int DEFAULT_NUM_PEOPLE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Radio buttons

        fifthteenP = (RadioButton) findViewById(R.id.fifthteenP);
        eighteenP = (RadioButton) findViewById(R.id.eighteenP);
        twentyP = (RadioButton) findViewById(R.id.twentyP);
        customAmt = (RadioButton) findViewById(R.id.custom);


        // Access the various widgets by their id in R.java
        amount = (EditText) findViewById(R.id.amount);
        //On app load, the cursor should be in the Amount field
        amount.requestFocus();
        amtOfPeople = (EditText) findViewById(R.id.people);
        customTipAmt = (EditText) findViewById(R.id.customTip);

        amtOfPeople.setText(Integer.toString(DEFAULT_NUM_PEOPLE));


        tipAmounts = (RadioGroup) findViewById(R.id.tipAmounts);

        submit = (Button) findViewById(R.id.submit);
        //On app load, the Calculate button is disabled
        submit.setEnabled(false);

        clear = (Button) findViewById(R.id.clear);

        tipTotalPerPerson = (TextView) findViewById(R.id.tipTotal);
        tipAmountFinal = (TextView) findViewById(R.id.tipAmountFinal);
        totalFinal = (TextView) findViewById(R.id.total);

        // On app load, disable the 'Other tip' percentage text field
        customTipAmt.setEnabled(false);


        tipAmounts.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // Enable/disable Other Percentage tip field
                if (checkedId == R.id.fifthteenP
                        || checkedId == R.id.eighteenP || checkedId == R.id.twentyP) {
                    customTipAmt.setEnabled(false);

                    submit.setEnabled(amount.getText().length() > 0
                            && amtOfPeople.getText().length() > 0);
                }
                if (checkedId == R.id.custom) {
                    // enable the Other Percentage tip field
                    customTipAmt.setEnabled(true);
                    // set the focus to this field
                    customTipAmt.requestFocus();

                    submit.setEnabled(amount.getText().length() > 0
                            && amtOfPeople.getText().length() > 0
                            && customTipAmt.getText().length() > 0);
                }
                // To determine the tip percentage choice made by user
                radioCheckedId = checkedId;
            }
        });

        /*
         * Attach a KeyListener to the Tip Amount, No. of People and Other Tip
         * Percentage text fields
         */
        amount.setOnKeyListener(mKeyListener);
        amtOfPeople.setOnKeyListener(mKeyListener);
        customTipAmt.setOnKeyListener(mKeyListener);

        submit.setOnClickListener(mClickListener);
        clear.setOnClickListener(mClickListener);


    }


    private View.OnKeyListener mKeyListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {

            switch (v.getId()) {
                case R.id.amount:
                case R.id.people:
                    submit.setEnabled(amount.getText().length() > 0
                            && amtOfPeople.getText().length() > 0);
                    break;
                case R.id.customTip:
                    submit.setEnabled(amount.getText().length() > 0
                            && amtOfPeople.getText().length() > 0
                            && customTipAmt.getText().length() > 0);
                    break;
            }
            return false;
        }

    };

    private OnClickListener mClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.submit) {
                calculate();
            } else {
                reset();
            }
        }
    };


    private void reset() {
        tipAmountFinal.setText("");
        totalFinal.setText("");
        tipTotalPerPerson.setText("");
        amount.setText("");
        amtOfPeople.setText(Integer.toString(DEFAULT_NUM_PEOPLE));
        customTipAmt.setText("");
        tipAmounts.clearCheck();
        tipAmounts.check(R.id.fifthteenP);
        // set focus on the first field
        amount.requestFocus();
    }


    private void calculate() {
        Double billAmount = Double.parseDouble(
                amount.getText().toString());
        Double totalPeople = Double.parseDouble(
                amtOfPeople.getText().toString());
        Double percentage = null;
        boolean isError = false;
        if (billAmount < 1.0) {
            showErrorAlert("Enter a valid Total Amount.",
                    amount.getId());
            isError = true;
        }

        if (totalPeople < 1.0) {
            showErrorAlert("Enter a valid number of people.",
                    amtOfPeople.getId());
            isError = true;
        }


        if (radioCheckedId == -1) {
            radioCheckedId = tipAmounts.getCheckedRadioButtonId();
        }
        if (radioCheckedId == R.id.fifthteenP) {
            percentage = 15.00;
        } else if (radioCheckedId == R.id.twentyP) {
            percentage = 20.00;
        } else if (radioCheckedId == R.id.eighteenP) {
            percentage = 18.00;
        } else {
            percentage = Double.parseDouble(customTipAmt.getText().toString());;

            if (percentage < 1.0) {
                showErrorAlert("Enter a valid Tip percentage",
                        customTipAmt.getId());
                isError = true;
            }
        }


        if (!isError) {
            double tipAmount = ((billAmount * percentage) / 100);
            double totalToPay = billAmount + tipAmount;
            double perPersonPays = totalToPay / totalPeople;


            tipAmountFinal.setText(Double.toString(tipAmount));
            totalFinal.setText(Double.toString(totalToPay));
            tipTotalPerPerson.setText(Double.toString(perPersonPays));
        }
    }

    private void showErrorAlert(String errorMessage, final int fieldId) {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(errorMessage)
                .setNeutralButton("Close",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                findViewById(fieldId).requestFocus();
                            }
                        }).show();
    }

}

