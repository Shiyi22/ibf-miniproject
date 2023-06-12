package ibfbatch2miniproject.backend.repository;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

@Repository
public class S3Repository {

    @Autowired
    private AmazonS3 s3Client;

    @Value("${do.storage.bucketname}")
    private String bucketName; 

    @Value("${do.storage.endpoint}")
    private String endPoint; 

    // save to S3 
    public String uploadToS3(Integer gameId, MultipartFile media) throws IOException {

        String partialUrl = "http://" + bucketName + "." + endPoint + "/";

        Map<String, String> userData = new HashMap<>(); 
        userData.put("gameId", gameId.toString()); 
        userData.put("uploadTime", new Date().toString());
        userData.put("originalFilename", media.getOriginalFilename());

        ObjectMetadata metaData = new ObjectMetadata();
        metaData.setContentLength(media.getSize());
        metaData.setContentType(media.getContentType());
        metaData.setUserMetadata(userData);

        String key = "gameMedia/%d/%s".formatted(gameId, media.getOriginalFilename());
        PutObjectRequest putReq = new PutObjectRequest(bucketName, key, media.getInputStream(), metaData);
        putReq = putReq.withCannedAcl(CannedAccessControlList.PublicRead);

        s3Client.putObject(putReq);
        return partialUrl + key; 
    }
    
}
