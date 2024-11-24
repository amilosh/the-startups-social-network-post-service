package faang.school.postservice.protobuf.generate;// Generated by the protocol buffer compiler.  DO NOT EDIT!
// NO CHECKED-IN PROTOBUF GENCODE
// source: album_created_event.proto
// Protobuf Java Version: 4.28.2

public final class AlbumCreatedEventProto {
  private AlbumCreatedEventProto() {}
  static {
    com.google.protobuf.RuntimeVersion.validateProtobufGencodeVersion(
      com.google.protobuf.RuntimeVersion.RuntimeDomain.PUBLIC,
      /* major= */ 4,
      /* minor= */ 28,
      /* patch= */ 2,
      /* suffix= */ "",
      AlbumCreatedEventProto.class.getName());
  }
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  public interface AlbumCreatedEventOrBuilder extends
      // @@protoc_insertion_point(interface_extends:AlbumCreatedEvent)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>int64 user_id = 1;</code>
     * @return The userId.
     */
    long getUserId();

    /**
     * <code>int64 album_id = 2;</code>
     * @return The albumId.
     */
    long getAlbumId();

    /**
     * <code>string album_name = 3;</code>
     * @return The albumName.
     */
    java.lang.String getAlbumName();
    /**
     * <code>string album_name = 3;</code>
     * @return The bytes for albumName.
     */
    com.google.protobuf.ByteString
        getAlbumNameBytes();

    /**
     * <code>.google.protobuf.Timestamp event_time = 4;</code>
     * @return Whether the eventTime field is set.
     */
    boolean hasEventTime();
    /**
     * <code>.google.protobuf.Timestamp event_time = 4;</code>
     * @return The eventTime.
     */
    com.google.protobuf.Timestamp getEventTime();
    /**
     * <code>.google.protobuf.Timestamp event_time = 4;</code>
     */
    com.google.protobuf.TimestampOrBuilder getEventTimeOrBuilder();
  }
  /**
   * Protobuf type {@code AlbumCreatedEvent}
   */
  public static final class AlbumCreatedEvent extends
      com.google.protobuf.GeneratedMessage implements
      // @@protoc_insertion_point(message_implements:AlbumCreatedEvent)
      AlbumCreatedEventOrBuilder {
  private static final long serialVersionUID = 0L;
    static {
      com.google.protobuf.RuntimeVersion.validateProtobufGencodeVersion(
        com.google.protobuf.RuntimeVersion.RuntimeDomain.PUBLIC,
        /* major= */ 4,
        /* minor= */ 28,
        /* patch= */ 2,
        /* suffix= */ "",
        AlbumCreatedEvent.class.getName());
    }
    // Use AlbumCreatedEvent.newBuilder() to construct.
    private AlbumCreatedEvent(com.google.protobuf.GeneratedMessage.Builder<?> builder) {
      super(builder);
    }
    private AlbumCreatedEvent() {
      albumName_ = "";
    }

    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return AlbumCreatedEventProto.internal_static_AlbumCreatedEvent_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return AlbumCreatedEventProto.internal_static_AlbumCreatedEvent_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              AlbumCreatedEventProto.AlbumCreatedEvent.class, AlbumCreatedEventProto.AlbumCreatedEvent.Builder.class);
    }

    private int bitField0_;
    public static final int USER_ID_FIELD_NUMBER = 1;
    private long userId_ = 0L;
    /**
     * <code>int64 user_id = 1;</code>
     * @return The userId.
     */
    @java.lang.Override
    public long getUserId() {
      return userId_;
    }

    public static final int ALBUM_ID_FIELD_NUMBER = 2;
    private long albumId_ = 0L;
    /**
     * <code>int64 album_id = 2;</code>
     * @return The albumId.
     */
    @java.lang.Override
    public long getAlbumId() {
      return albumId_;
    }

    public static final int ALBUM_NAME_FIELD_NUMBER = 3;
    @SuppressWarnings("serial")
    private volatile java.lang.Object albumName_ = "";
    /**
     * <code>string album_name = 3;</code>
     * @return The albumName.
     */
    @java.lang.Override
    public java.lang.String getAlbumName() {
      java.lang.Object ref = albumName_;
      if (ref instanceof java.lang.String) {
        return (java.lang.String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        albumName_ = s;
        return s;
      }
    }
    /**
     * <code>string album_name = 3;</code>
     * @return The bytes for albumName.
     */
    @java.lang.Override
    public com.google.protobuf.ByteString
        getAlbumNameBytes() {
      java.lang.Object ref = albumName_;
      if (ref instanceof java.lang.String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        albumName_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    public static final int EVENT_TIME_FIELD_NUMBER = 4;
    private com.google.protobuf.Timestamp eventTime_;
    /**
     * <code>.google.protobuf.Timestamp event_time = 4;</code>
     * @return Whether the eventTime field is set.
     */
    @java.lang.Override
    public boolean hasEventTime() {
      return ((bitField0_ & 0x00000001) != 0);
    }
    /**
     * <code>.google.protobuf.Timestamp event_time = 4;</code>
     * @return The eventTime.
     */
    @java.lang.Override
    public com.google.protobuf.Timestamp getEventTime() {
      return eventTime_ == null ? com.google.protobuf.Timestamp.getDefaultInstance() : eventTime_;
    }
    /**
     * <code>.google.protobuf.Timestamp event_time = 4;</code>
     */
    @java.lang.Override
    public com.google.protobuf.TimestampOrBuilder getEventTimeOrBuilder() {
      return eventTime_ == null ? com.google.protobuf.Timestamp.getDefaultInstance() : eventTime_;
    }

    private byte memoizedIsInitialized = -1;
    @java.lang.Override
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized == 1) return true;
      if (isInitialized == 0) return false;

      memoizedIsInitialized = 1;
      return true;
    }

    @java.lang.Override
    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      if (userId_ != 0L) {
        output.writeInt64(1, userId_);
      }
      if (albumId_ != 0L) {
        output.writeInt64(2, albumId_);
      }
      if (!com.google.protobuf.GeneratedMessage.isStringEmpty(albumName_)) {
        com.google.protobuf.GeneratedMessage.writeString(output, 3, albumName_);
      }
      if (((bitField0_ & 0x00000001) != 0)) {
        output.writeMessage(4, getEventTime());
      }
      getUnknownFields().writeTo(output);
    }

    @java.lang.Override
    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (userId_ != 0L) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt64Size(1, userId_);
      }
      if (albumId_ != 0L) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt64Size(2, albumId_);
      }
      if (!com.google.protobuf.GeneratedMessage.isStringEmpty(albumName_)) {
        size += com.google.protobuf.GeneratedMessage.computeStringSize(3, albumName_);
      }
      if (((bitField0_ & 0x00000001) != 0)) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(4, getEventTime());
      }
      size += getUnknownFields().getSerializedSize();
      memoizedSize = size;
      return size;
    }

    @java.lang.Override
    public boolean equals(final java.lang.Object obj) {
      if (obj == this) {
       return true;
      }
      if (!(obj instanceof AlbumCreatedEventProto.AlbumCreatedEvent)) {
        return super.equals(obj);
      }
      AlbumCreatedEventProto.AlbumCreatedEvent other = (AlbumCreatedEventProto.AlbumCreatedEvent) obj;

      if (getUserId()
          != other.getUserId()) return false;
      if (getAlbumId()
          != other.getAlbumId()) return false;
      if (!getAlbumName()
          .equals(other.getAlbumName())) return false;
      if (hasEventTime() != other.hasEventTime()) return false;
      if (hasEventTime()) {
        if (!getEventTime()
            .equals(other.getEventTime())) return false;
      }
      if (!getUnknownFields().equals(other.getUnknownFields())) return false;
      return true;
    }

    @java.lang.Override
    public int hashCode() {
      if (memoizedHashCode != 0) {
        return memoizedHashCode;
      }
      int hash = 41;
      hash = (19 * hash) + getDescriptor().hashCode();
      hash = (37 * hash) + USER_ID_FIELD_NUMBER;
      hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
          getUserId());
      hash = (37 * hash) + ALBUM_ID_FIELD_NUMBER;
      hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
          getAlbumId());
      hash = (37 * hash) + ALBUM_NAME_FIELD_NUMBER;
      hash = (53 * hash) + getAlbumName().hashCode();
      if (hasEventTime()) {
        hash = (37 * hash) + EVENT_TIME_FIELD_NUMBER;
        hash = (53 * hash) + getEventTime().hashCode();
      }
      hash = (29 * hash) + getUnknownFields().hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static AlbumCreatedEventProto.AlbumCreatedEvent parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static AlbumCreatedEventProto.AlbumCreatedEvent parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static AlbumCreatedEventProto.AlbumCreatedEvent parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static AlbumCreatedEventProto.AlbumCreatedEvent parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static AlbumCreatedEventProto.AlbumCreatedEvent parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static AlbumCreatedEventProto.AlbumCreatedEvent parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static AlbumCreatedEventProto.AlbumCreatedEvent parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessage
          .parseWithIOException(PARSER, input);
    }
    public static AlbumCreatedEventProto.AlbumCreatedEvent parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessage
          .parseWithIOException(PARSER, input, extensionRegistry);
    }

    public static AlbumCreatedEventProto.AlbumCreatedEvent parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessage
          .parseDelimitedWithIOException(PARSER, input);
    }

    public static AlbumCreatedEventProto.AlbumCreatedEvent parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessage
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static AlbumCreatedEventProto.AlbumCreatedEvent parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessage
          .parseWithIOException(PARSER, input);
    }
    public static AlbumCreatedEventProto.AlbumCreatedEvent parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessage
          .parseWithIOException(PARSER, input, extensionRegistry);
    }

    @java.lang.Override
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    public static Builder newBuilder(AlbumCreatedEventProto.AlbumCreatedEvent prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
    }
    @java.lang.Override
    public Builder toBuilder() {
      return this == DEFAULT_INSTANCE
          ? new Builder() : new Builder().mergeFrom(this);
    }

    @java.lang.Override
    protected Builder newBuilderForType(
        com.google.protobuf.GeneratedMessage.BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    /**
     * Protobuf type {@code AlbumCreatedEvent}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessage.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:AlbumCreatedEvent)
        AlbumCreatedEventProto.AlbumCreatedEventOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return AlbumCreatedEventProto.internal_static_AlbumCreatedEvent_descriptor;
      }

      @java.lang.Override
      protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return AlbumCreatedEventProto.internal_static_AlbumCreatedEvent_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                AlbumCreatedEventProto.AlbumCreatedEvent.class, AlbumCreatedEventProto.AlbumCreatedEvent.Builder.class);
      }

      // Construct using faang.school.postservice.protobuf.generate.AlbumCreatedEventProto.AlbumCreatedEvent.newBuilder()
      private Builder() {
        maybeForceBuilderInitialization();
      }

      private Builder(
          com.google.protobuf.GeneratedMessage.BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessage
                .alwaysUseFieldBuilders) {
          getEventTimeFieldBuilder();
        }
      }
      @java.lang.Override
      public Builder clear() {
        super.clear();
        bitField0_ = 0;
        userId_ = 0L;
        albumId_ = 0L;
        albumName_ = "";
        eventTime_ = null;
        if (eventTimeBuilder_ != null) {
          eventTimeBuilder_.dispose();
          eventTimeBuilder_ = null;
        }
        return this;
      }

      @java.lang.Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return AlbumCreatedEventProto.internal_static_AlbumCreatedEvent_descriptor;
      }

      @java.lang.Override
      public AlbumCreatedEventProto.AlbumCreatedEvent getDefaultInstanceForType() {
        return AlbumCreatedEventProto.AlbumCreatedEvent.getDefaultInstance();
      }

      @java.lang.Override
      public AlbumCreatedEventProto.AlbumCreatedEvent build() {
        AlbumCreatedEventProto.AlbumCreatedEvent result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @java.lang.Override
      public AlbumCreatedEventProto.AlbumCreatedEvent buildPartial() {
        AlbumCreatedEventProto.AlbumCreatedEvent result = new AlbumCreatedEventProto.AlbumCreatedEvent(this);
        if (bitField0_ != 0) { buildPartial0(result); }
        onBuilt();
        return result;
      }

      private void buildPartial0(AlbumCreatedEventProto.AlbumCreatedEvent result) {
        int from_bitField0_ = bitField0_;
        if (((from_bitField0_ & 0x00000001) != 0)) {
          result.userId_ = userId_;
        }
        if (((from_bitField0_ & 0x00000002) != 0)) {
          result.albumId_ = albumId_;
        }
        if (((from_bitField0_ & 0x00000004) != 0)) {
          result.albumName_ = albumName_;
        }
        int to_bitField0_ = 0;
        if (((from_bitField0_ & 0x00000008) != 0)) {
          result.eventTime_ = eventTimeBuilder_ == null
              ? eventTime_
              : eventTimeBuilder_.build();
          to_bitField0_ |= 0x00000001;
        }
        result.bitField0_ |= to_bitField0_;
      }

      @java.lang.Override
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof AlbumCreatedEventProto.AlbumCreatedEvent) {
          return mergeFrom((AlbumCreatedEventProto.AlbumCreatedEvent)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(AlbumCreatedEventProto.AlbumCreatedEvent other) {
        if (other == AlbumCreatedEventProto.AlbumCreatedEvent.getDefaultInstance()) return this;
        if (other.getUserId() != 0L) {
          setUserId(other.getUserId());
        }
        if (other.getAlbumId() != 0L) {
          setAlbumId(other.getAlbumId());
        }
        if (!other.getAlbumName().isEmpty()) {
          albumName_ = other.albumName_;
          bitField0_ |= 0x00000004;
          onChanged();
        }
        if (other.hasEventTime()) {
          mergeEventTime(other.getEventTime());
        }
        this.mergeUnknownFields(other.getUnknownFields());
        onChanged();
        return this;
      }

      @java.lang.Override
      public final boolean isInitialized() {
        return true;
      }

      @java.lang.Override
      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        if (extensionRegistry == null) {
          throw new java.lang.NullPointerException();
        }
        try {
          boolean done = false;
          while (!done) {
            int tag = input.readTag();
            switch (tag) {
              case 0:
                done = true;
                break;
              case 8: {
                userId_ = input.readInt64();
                bitField0_ |= 0x00000001;
                break;
              } // case 8
              case 16: {
                albumId_ = input.readInt64();
                bitField0_ |= 0x00000002;
                break;
              } // case 16
              case 26: {
                albumName_ = input.readStringRequireUtf8();
                bitField0_ |= 0x00000004;
                break;
              } // case 26
              case 34: {
                input.readMessage(
                    getEventTimeFieldBuilder().getBuilder(),
                    extensionRegistry);
                bitField0_ |= 0x00000008;
                break;
              } // case 34
              default: {
                if (!super.parseUnknownField(input, extensionRegistry, tag)) {
                  done = true; // was an endgroup tag
                }
                break;
              } // default:
            } // switch (tag)
          } // while (!done)
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          throw e.unwrapIOException();
        } finally {
          onChanged();
        } // finally
        return this;
      }
      private int bitField0_;

      private long userId_ ;
      /**
       * <code>int64 user_id = 1;</code>
       * @return The userId.
       */
      @java.lang.Override
      public long getUserId() {
        return userId_;
      }
      /**
       * <code>int64 user_id = 1;</code>
       * @param value The userId to set.
       * @return This builder for chaining.
       */
      public Builder setUserId(long value) {

        userId_ = value;
        bitField0_ |= 0x00000001;
        onChanged();
        return this;
      }
      /**
       * <code>int64 user_id = 1;</code>
       * @return This builder for chaining.
       */
      public Builder clearUserId() {
        bitField0_ = (bitField0_ & ~0x00000001);
        userId_ = 0L;
        onChanged();
        return this;
      }

      private long albumId_ ;
      /**
       * <code>int64 album_id = 2;</code>
       * @return The albumId.
       */
      @java.lang.Override
      public long getAlbumId() {
        return albumId_;
      }
      /**
       * <code>int64 album_id = 2;</code>
       * @param value The albumId to set.
       * @return This builder for chaining.
       */
      public Builder setAlbumId(long value) {

        albumId_ = value;
        bitField0_ |= 0x00000002;
        onChanged();
        return this;
      }
      /**
       * <code>int64 album_id = 2;</code>
       * @return This builder for chaining.
       */
      public Builder clearAlbumId() {
        bitField0_ = (bitField0_ & ~0x00000002);
        albumId_ = 0L;
        onChanged();
        return this;
      }

      private java.lang.Object albumName_ = "";
      /**
       * <code>string album_name = 3;</code>
       * @return The albumName.
       */
      public java.lang.String getAlbumName() {
        java.lang.Object ref = albumName_;
        if (!(ref instanceof java.lang.String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          java.lang.String s = bs.toStringUtf8();
          albumName_ = s;
          return s;
        } else {
          return (java.lang.String) ref;
        }
      }
      /**
       * <code>string album_name = 3;</code>
       * @return The bytes for albumName.
       */
      public com.google.protobuf.ByteString
          getAlbumNameBytes() {
        java.lang.Object ref = albumName_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (java.lang.String) ref);
          albumName_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>string album_name = 3;</code>
       * @param value The albumName to set.
       * @return This builder for chaining.
       */
      public Builder setAlbumName(
          java.lang.String value) {
        if (value == null) { throw new NullPointerException(); }
        albumName_ = value;
        bitField0_ |= 0x00000004;
        onChanged();
        return this;
      }
      /**
       * <code>string album_name = 3;</code>
       * @return This builder for chaining.
       */
      public Builder clearAlbumName() {
        albumName_ = getDefaultInstance().getAlbumName();
        bitField0_ = (bitField0_ & ~0x00000004);
        onChanged();
        return this;
      }
      /**
       * <code>string album_name = 3;</code>
       * @param value The bytes for albumName to set.
       * @return This builder for chaining.
       */
      public Builder setAlbumNameBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) { throw new NullPointerException(); }
        checkByteStringIsUtf8(value);
        albumName_ = value;
        bitField0_ |= 0x00000004;
        onChanged();
        return this;
      }

      private com.google.protobuf.Timestamp eventTime_;
      private com.google.protobuf.SingleFieldBuilder<
          com.google.protobuf.Timestamp, com.google.protobuf.Timestamp.Builder, com.google.protobuf.TimestampOrBuilder> eventTimeBuilder_;
      /**
       * <code>.google.protobuf.Timestamp event_time = 4;</code>
       * @return Whether the eventTime field is set.
       */
      public boolean hasEventTime() {
        return ((bitField0_ & 0x00000008) != 0);
      }
      /**
       * <code>.google.protobuf.Timestamp event_time = 4;</code>
       * @return The eventTime.
       */
      public com.google.protobuf.Timestamp getEventTime() {
        if (eventTimeBuilder_ == null) {
          return eventTime_ == null ? com.google.protobuf.Timestamp.getDefaultInstance() : eventTime_;
        } else {
          return eventTimeBuilder_.getMessage();
        }
      }
      /**
       * <code>.google.protobuf.Timestamp event_time = 4;</code>
       */
      public Builder setEventTime(com.google.protobuf.Timestamp value) {
        if (eventTimeBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          eventTime_ = value;
        } else {
          eventTimeBuilder_.setMessage(value);
        }
        bitField0_ |= 0x00000008;
        onChanged();
        return this;
      }
      /**
       * <code>.google.protobuf.Timestamp event_time = 4;</code>
       */
      public Builder setEventTime(
          com.google.protobuf.Timestamp.Builder builderForValue) {
        if (eventTimeBuilder_ == null) {
          eventTime_ = builderForValue.build();
        } else {
          eventTimeBuilder_.setMessage(builderForValue.build());
        }
        bitField0_ |= 0x00000008;
        onChanged();
        return this;
      }
      /**
       * <code>.google.protobuf.Timestamp event_time = 4;</code>
       */
      public Builder mergeEventTime(com.google.protobuf.Timestamp value) {
        if (eventTimeBuilder_ == null) {
          if (((bitField0_ & 0x00000008) != 0) &&
            eventTime_ != null &&
            eventTime_ != com.google.protobuf.Timestamp.getDefaultInstance()) {
            getEventTimeBuilder().mergeFrom(value);
          } else {
            eventTime_ = value;
          }
        } else {
          eventTimeBuilder_.mergeFrom(value);
        }
        if (eventTime_ != null) {
          bitField0_ |= 0x00000008;
          onChanged();
        }
        return this;
      }
      /**
       * <code>.google.protobuf.Timestamp event_time = 4;</code>
       */
      public Builder clearEventTime() {
        bitField0_ = (bitField0_ & ~0x00000008);
        eventTime_ = null;
        if (eventTimeBuilder_ != null) {
          eventTimeBuilder_.dispose();
          eventTimeBuilder_ = null;
        }
        onChanged();
        return this;
      }
      /**
       * <code>.google.protobuf.Timestamp event_time = 4;</code>
       */
      public com.google.protobuf.Timestamp.Builder getEventTimeBuilder() {
        bitField0_ |= 0x00000008;
        onChanged();
        return getEventTimeFieldBuilder().getBuilder();
      }
      /**
       * <code>.google.protobuf.Timestamp event_time = 4;</code>
       */
      public com.google.protobuf.TimestampOrBuilder getEventTimeOrBuilder() {
        if (eventTimeBuilder_ != null) {
          return eventTimeBuilder_.getMessageOrBuilder();
        } else {
          return eventTime_ == null ?
              com.google.protobuf.Timestamp.getDefaultInstance() : eventTime_;
        }
      }
      /**
       * <code>.google.protobuf.Timestamp event_time = 4;</code>
       */
      private com.google.protobuf.SingleFieldBuilder<
          com.google.protobuf.Timestamp, com.google.protobuf.Timestamp.Builder, com.google.protobuf.TimestampOrBuilder> 
          getEventTimeFieldBuilder() {
        if (eventTimeBuilder_ == null) {
          eventTimeBuilder_ = new com.google.protobuf.SingleFieldBuilder<
              com.google.protobuf.Timestamp, com.google.protobuf.Timestamp.Builder, com.google.protobuf.TimestampOrBuilder>(
                  getEventTime(),
                  getParentForChildren(),
                  isClean());
          eventTime_ = null;
        }
        return eventTimeBuilder_;
      }

      // @@protoc_insertion_point(builder_scope:AlbumCreatedEvent)
    }

    // @@protoc_insertion_point(class_scope:AlbumCreatedEvent)
    private static final AlbumCreatedEventProto.AlbumCreatedEvent DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new AlbumCreatedEventProto.AlbumCreatedEvent();
    }

    public static AlbumCreatedEventProto.AlbumCreatedEvent getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<AlbumCreatedEvent>
        PARSER = new com.google.protobuf.AbstractParser<AlbumCreatedEvent>() {
      @java.lang.Override
      public AlbumCreatedEvent parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        Builder builder = newBuilder();
        try {
          builder.mergeFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          throw e.setUnfinishedMessage(builder.buildPartial());
        } catch (com.google.protobuf.UninitializedMessageException e) {
          throw e.asInvalidProtocolBufferException().setUnfinishedMessage(builder.buildPartial());
        } catch (java.io.IOException e) {
          throw new com.google.protobuf.InvalidProtocolBufferException(e)
              .setUnfinishedMessage(builder.buildPartial());
        }
        return builder.buildPartial();
      }
    };

    public static com.google.protobuf.Parser<AlbumCreatedEvent> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<AlbumCreatedEvent> getParserForType() {
      return PARSER;
    }

    @java.lang.Override
    public AlbumCreatedEventProto.AlbumCreatedEvent getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_AlbumCreatedEvent_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_AlbumCreatedEvent_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\031album_created_event.proto\032\037google/prot" +
      "obuf/timestamp.proto\"z\n\021AlbumCreatedEven" +
      "t\022\017\n\007user_id\030\001 \001(\003\022\020\n\010album_id\030\002 \001(\003\022\022\n\n" +
      "album_name\030\003 \001(\t\022.\n\nevent_time\030\004 \001(\0132\032.g" +
      "oogle.protobuf.TimestampB\030B\026AlbumCreated" +
      "EventProtob\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
          com.google.protobuf.TimestampProto.getDescriptor(),
        });
    internal_static_AlbumCreatedEvent_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_AlbumCreatedEvent_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessage.FieldAccessorTable(
        internal_static_AlbumCreatedEvent_descriptor,
        new java.lang.String[] { "UserId", "AlbumId", "AlbumName", "EventTime", });
    descriptor.resolveAllFeaturesImmutable();
    com.google.protobuf.TimestampProto.getDescriptor();
  }

  // @@protoc_insertion_point(outer_class_scope)
}