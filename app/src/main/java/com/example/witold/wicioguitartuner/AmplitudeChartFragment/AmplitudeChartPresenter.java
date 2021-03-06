package com.example.witold.wicioguitartuner.AmplitudeChartFragment;



import com.example.witold.wicioguitartuner.AudioUtils.AudioAnalysis.AudioAnalysis;
import com.example.witold.wicioguitartuner.AudioUtils.AudioAnalysis.Complex;
import com.example.witold.wicioguitartuner.AudioUtils.AudioRecorder.AudioRecorderRxWrapper;
import com.example.witold.wicioguitartuner.Utils.RxBus.RxBus;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by Witold on 28.09.2017.
 */

public class AmplitudeChartPresenter implements AmplitudeChartFragmentContract.Presenter {
    private AmplitudeChartFragmentContract.AmplitudeChartView amplitudeChartView;
    private AudioRecorderRxWrapper audioRecorderRxWrapper;
    private RxBus rxBus;
    private CompositeDisposable subscriptions;

    @Inject
    public AmplitudeChartPresenter(AudioRecorderRxWrapper audioRecorderRxWrapper, RxBus rxBus) {
        this.audioRecorderRxWrapper = audioRecorderRxWrapper;
        this.rxBus = rxBus;
        subscriptions = new CompositeDisposable();
    }

    @Override
    public void onViewAttached(AmplitudeChartFragmentContract.AmplitudeChartView view) {
        this.amplitudeChartView = view;
        Timber.tag("Amplitude Presenter").d(audioRecorderRxWrapper + "");
    }

    @Override
    public void onViewDetached() {
        subscriptions.clear();
    }

    @Override
    public void subscribeAudioRecorder() {
        Disposable subscription = audioRecorderRxWrapper.getRecorderObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(sample -> AudioAnalysis.getComplexResult(sample, sample.length))
                .subscribe(
                        // onNext
                        this::processSamples,
                        // onError
                        throwable -> throwable.printStackTrace());
        subscriptions.add(subscription);
    }

    @Override
    public void subscribeRecordingEventBus() {
        rxBus.getEventObservable().subscribe(event -> subscribeAudioRecorder());
    }

    private void processSamples(Complex[] sample){
        amplitudeChartView.showDataOnChart(sample, sample.length);
    }
}
