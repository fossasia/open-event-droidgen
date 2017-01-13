var $generateBtn = $("#generate-btn"),
    $emailInput = $("#email"),
    $jsonUploadInput = $("#json-upload"),
    $apiEndpointInput = $("#api-endpoint"),
    $jsonUploadInputHolder = $("#json-upload-holder"),
    $apiEndpointInputHolder = $("#api-endpoint-holder"),
    $downloadBtn = $("#download-btn"),
    $form = $("#form"),
    $dataSourceRadio = $("input:radio[name=data-source]"),
    dataSourceType = null;

var $fileProgressHolder = $("#file-progress"),
    $fileProgressBar = $("#file-progress-bar"),
    $fileProgressVal = $("#file-progress-val");

var $generatorProgressHolder = $("#generator-progress"),
    $generatorProgressBar = $("#generator-progress-bar"),
    $generatorProgressVal = $("#generator-progress-val");

initialState();
$dataSourceRadio.change(
    function () {
        enableGenerateButton(false);
        $apiEndpointInput.val("");
        if (this.checked) {
            dataSourceType = $(this).val();
            if (dataSourceType === 'json_upload') {
                $jsonUploadInputHolder.show();
                $apiEndpointInputHolder.hide();
            }
            if (dataSourceType === 'api_endpoint') {
                $apiEndpointInputHolder.show();
                $jsonUploadInputHolder.hide();
            }
        }
    }
);

$apiEndpointInput.valueChange(function (value) {
    if (dataSourceType === 'api_endpoint') {
        if (value.trim() !== "" && isLink(value.trim())) {
            enableGenerateButton(true);
        } else {
            enableGenerateButton(false);
        }
    }
});

$jsonUploadInput.change(function () {
    if (dataSourceType === 'json_upload') {
        if (this.value !== "") {
            enableGenerateButton(true);
        } else {
            enableGenerateButton(false);
        }
    }
});

$generateBtn.click(function () {
    $('#generator-progress').show();
    $('#generator-progress-bar').show();
});


function initialState() {
    $dataSourceRadio.prop('checked', false);
    $generateBtn.prop('disabled', true);
}

function enableGenerateButton(enabled) {
    $generateBtn.prop('disabled', !enabled);
    if (enabled) {
        $generateBtn.attr('title', 'Generate app')
    }
    else {
        $generateBtn.attr('title', 'Select a zip to upload first')
    }
}

function showDownloadButton() {
    $generateBtn.hide();
    $downloadBtn.show();
}

function fileProgressUpdater(progressEvent) {
    var percentCompleted = Math.round((progressEvent.loaded * 100) / progressEvent.total);
    $fileProgressBar.css('width', percentCompleted + '%');
    $fileProgressVal.text(percentCompleted + '%');
}

$form.submit(function (e) {
    e.preventDefault();
    var data = new FormData();
    data.append('email', $emailInput.val());
    data.append('data-source', dataSourceType);

    var config = {};

    if (dataSourceType === 'json_upload') {
        data.append('json-upload', $jsonUploadInput[0].files[0]);
        $fileProgressHolder.show();
        config.onUploadProgress = fileProgressUpdater;
    } else {
        data.append('api-endpoint', $apiEndpointInput.val());
    }

    axios
        .post('/', data, config)
        .then(function (res) {
            showDownloadButton();
        })
        .catch(function (err) {
            output.className = 'container text-danger';
            output.innerHTML = err.message;
        });
});