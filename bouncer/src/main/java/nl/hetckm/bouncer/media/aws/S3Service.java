package nl.hetckm.bouncer.media.aws;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import nl.hetckm.bouncer.media.StorageSolution;
import nl.hetckm.bouncer.media.model.UploadResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
public class S3Service implements StorageSolution {

    private AmazonS3 client;

    @Value("${S3.endpoint.url}")
    private String endpoint;

    @Value("${S3.endpoint.region}")
    private String region;

    @Value("${S3.bucket}")
    private String bucket;

    @Value("${S3.credentials.access-key}")
    private String accessKey;

    @Value("${S3.credentials.secret-key}")
    private String secretKey;

    @PostConstruct
    public void init() {
        AWSCredentials credentials = new BasicAWSCredentials(
                accessKey,
                secretKey
        );

        EndpointConfiguration endpointConfiguration = new EndpointConfiguration(endpoint, region);

        client = AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withEndpointConfiguration(endpointConfiguration)
                .build();
    }

    @Override
    public UploadResult upload(byte[] data, String filename) {
        InputStream inputStream = new ByteArrayInputStream(data);

        String path =  UUID.randomUUID().toString();
        String contentType = getContentType(data);

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(data.length);
        objectMetadata.setContentType(contentType);

        client.putObject(bucket, path, inputStream, objectMetadata);

        return new UploadResult(path, contentType, data.length);
    }

    @Override
    public byte[] download(String path) throws IOException {
        S3Object object = client.getObject(bucket, path);
        S3ObjectInputStream inputStream = object.getObjectContent();
        return inputStream.readAllBytes();
    }

    @Override
    public void remove(String path) {
        client.deleteObject(bucket, path);
    }

}
