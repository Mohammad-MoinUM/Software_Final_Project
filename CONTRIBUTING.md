# Contributing to Mini Marketplace

Thank you for your interest in contributing to Mini Marketplace! This document provides guidelines for contributing to the project.

## Development Workflow

### Branch Strategy

We follow a Git Flow branching model:

- **main** - Production-ready code, protected branch
- **develop** - Development branch, integration branch for features
- **feature/** - Feature branches, created from develop
- **hotfix/** - Emergency fixes, created from main

### Creating a Feature Branch

1. Update your local develop branch:
   ```bash
   git checkout develop
   git pull origin develop
   ```

2. Create a new feature branch:
   ```bash
   git checkout -b feature/your-feature-name
   ```

3. Make your changes and commit:
   ```bash
   git add .
   git commit -m "feat: add your feature description"
   ```

4. Push your branch:
   ```bash
   git push origin feature/your-feature-name
   ```

### Pull Request Process

1. **Create Pull Request**
   - Title should be clear and descriptive
   - Description should explain what and why
   - Link any related issues

2. **Code Review**
   - At least one approval required
   - Address all review comments
   - Ensure all CI checks pass

3. **Merge**
   - Use "Squash and merge" for feature branches
   - Delete branch after merging

## Commit Message Convention

We follow the [Conventional Commits](https://www.conventionalcommits.org/) specification:

```
<type>(<scope>): <subject>

<body>

<footer>
```

### Types

- **feat:** New feature
- **fix:** Bug fix
- **docs:** Documentation changes
- **style:** Code style changes (formatting, etc.)
- **refactor:** Code refactoring
- **test:** Adding or updating tests
- **chore:** Maintenance tasks

### Examples

```bash
feat(user): add user registration endpoint
fix(product): resolve stock quantity calculation error
docs: update API documentation
test(order): add integration tests for order cancellation
```

## Coding Standards

### Java Code Style

- Follow standard Java naming conventions
- Use meaningful variable and method names
- Keep methods small and focused (Single Responsibility)
- Add JavaDoc comments for public APIs
- Maximum line length: 120 characters

### Spring Boot Best Practices

- Use constructor injection (avoid @Autowired on fields)
- Use DTOs for API requests/responses
- Implement proper exception handling
- Use appropriate HTTP status codes
- Add validation annotations where needed

### Example

```java
/**
 * Service for managing user operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    /**
     * Create a new user
     * @param user User entity to create
     * @return Created user
     */
    public User createUser(User user) {
        log.info("Creating new user: {}", user.getUsername());
        // Implementation
    }
}
```

## Testing Requirements

### Unit Tests

- Write unit tests for all service methods
- Use Mockito for mocking dependencies
- Aim for >80% code coverage
- Test both success and failure scenarios

### Integration Tests

- Write integration tests for REST endpoints
- Use `@SpringBootTest` and `MockMvc`
- Test authentication and authorization
- Verify response structure and status codes

### Running Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=UserServiceTest

# Run with coverage
mvn test jacoco:report
```

## Documentation

### API Documentation

- Document all REST endpoints
- Include request/response examples
- Specify required authentication
- Document error responses

### Code Documentation

- Add JavaDoc for public classes and methods
- Explain complex logic with inline comments
- Keep comments up-to-date

## Docker Guidelines

### Dockerfile
- Use multi-stage builds
- Follow security best practices
- Minimize image size
- Use specific version tags

### Docker Compose
- Use environment variables
- No hardcoded credentials
- Include health checks
- Document all services

## Questions?

If you have questions, please:
1. Check existing documentation
2. Search closed issues
3. Open a new issue for discussion

## Code of Conduct

- Be respectful and professional
- Welcome newcomers
- Focus on constructive feedback
- Collaborate effectively

Thank you for contributing! 🎉
