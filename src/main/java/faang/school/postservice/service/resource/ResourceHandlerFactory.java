package faang.school.postservice.service.resource;

import faang.school.postservice.dto.resource.ResourceType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ResourceHandlerFactory {
    private final Map<ResourceType, ResourceHandler> handlers;

    public ResourceHandlerFactory(List<ResourceHandler> handlers) {
        this.handlers = handlers.stream()
                .collect(Collectors.toMap(ResourceHandler::getType, Function.identity()));
    }

    public ResourceHandler getHandler(ResourceType type) {
        ResourceHandler handler = handlers.get(type);
        log.info("handler for the type {} was assigned by ResourceHandleFactory", type);
        if (handler == null) {
            log.info("handler for the type {} was not found", type);
            throw new IllegalArgumentException("No handler found for type: " + type);
        }
        return handler;
    }
}