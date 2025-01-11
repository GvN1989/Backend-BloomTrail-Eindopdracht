package nl.novi.bloomtrail.common;

import nl.novi.bloomtrail.models.Upload;

import java.util.List;

public interface Uploadable {
    List<Upload> getUploads();
    void addUpload(Upload upload);
    void removeUpload(Upload upload);
    String generateDownloadUrl();
}
