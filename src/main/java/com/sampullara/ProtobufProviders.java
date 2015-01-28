package com.sampullara;

import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.Message;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * These providers implement the conversion of protobuf objects to and from their serialized form over the wire.
 *  
 * User: sam
 * Date: Dec 27, 2008
 * Time: 3:13:17 PM
 */
public class ProtobufProviders {
    @Provider
    @Consumes("application/x-protobuf")
    public static class ProtobufMessageBodyReader implements MessageBodyReader<Message> {
    	@Override
        public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
            return Message.class.isAssignableFrom(type);
        }
    	@Override
        public Message readFrom(Class<Message> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException {
            try {
                Method newBuilder = type.getMethod("newBuilder");
                GeneratedMessage.Builder builder = (GeneratedMessage.Builder) newBuilder.invoke(type);
                return builder.mergeFrom(entityStream).build();
            } catch (Exception e) {
                throw new WebApplicationException(e);
            }
        }
    }

    @Provider
    @Produces("application/x-protobuf")
    public static class ProtobufMessageBodyWriter implements MessageBodyWriter<Message> {
    	@Override
    	public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
            return Message.class.isAssignableFrom(type);
        }

        private Map<Object, byte[]> buffer = new WeakHashMap<Object, byte[]>();
        
        @Override
        public long getSize(Message m, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                m.writeTo(baos);
            } catch (IOException e) {
                return -1;
            }
            byte[] bytes = baos.toByteArray();
            buffer.put(m, bytes);
            return bytes.length;
        }
        @Override
        public void writeTo(Message m, Class type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
            entityStream.write(buffer.remove(m));
        }
    }
}
