package pl.databucket.exception;

import org.springframework.boot.autoconfigure.couchbase.CouchbaseProperties;

import static pl.databucket.exception.AlreadyExistingItem.BUCKET;

@SuppressWarnings("serial")
public class BucketAlreadyExistsException extends AlreadyExistsException {

	public BucketAlreadyExistsException(String bucketName) {
        super(BUCKET, bucketName);
    }

}
