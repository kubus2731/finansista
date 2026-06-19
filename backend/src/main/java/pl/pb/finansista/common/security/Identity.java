package pl.pb.finansista.common.security;

import java.util.UUID;

public record Identity(UUID id, String role, boolean active) {}
