package nl.novi.bloomtrail.services;

import nl.novi.bloomtrail.common.Downloadable;
import nl.novi.bloomtrail.common.Uploadable;
import nl.novi.bloomtrail.models.Upload;
import org.springframework.stereotype.Service;

@Service
public class AssignmentService {

    @Override
    public String generateDownloadUrl() {
        return null;
    }


    @Override
    public void addUpload(Upload upload) {
        uploads.add(upload);
        upload.setAssignment(this);
    }
    @Override
    public void removeUpload(Upload upload) {
        uploads.remove(upload);
        upload.setAssignment(null);
    }


    @Override
    public String getDownload() {
        return null;
    }

    @Override
    public void setDownload(String download) {

    }

}

// Assign assignment -> AssignmentService